package cu.sitrans.viajero.ui.trip

import androidx.lifecycle.ViewModel;
import cu.sitrans.viajero.mvi.MviViewModel
import cu.sitrans.viajero.mvi.action_procesor.TripActionProcessorHolder
import cu.sitrans.viajero.utils.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject


class TripListViewModel @Inject constructor(val processorHolder: TripActionProcessorHolder) : ViewModel(),
    MviViewModel<TripIntent, TripViewState> {
    /**
     * Proxy subject used to keep the stream alive even after the UI gets recycled.
     * This is basically used to keep ongoing events and the last cached State alive
     * while the UI disconnects and reconnects on config changes.
     */
    private val intentsSubject: PublishSubject<TripIntent> = PublishSubject.create()
    private val statesObservable: Observable<TripViewState> = compose()


    /**
     * take only the first ever InitialIntent and all intents of other types
     * to avoid reloading data on config changes
     */
    private val intentFilter: ObservableTransformer<TripIntent, TripIntent>
        get() = ObservableTransformer { intents ->
            intents.publish { shared ->
                Observable.merge(
                    shared.ofType(TripIntent.InitialIntent::class.java).take(1),
                    shared.notOfType(TripIntent.InitialIntent::class.java)
                )
            }
        }


    /**
     * Compose all components to create the stream logic
     */
    private fun compose(): Observable<TripViewState> {
        return intentsSubject
            //.compose(intentFilter)
            .map(this::actionFromIntent)
            .compose(processorHolder.actionProcessor)
            // Cache each state and pass it to the reducer to create a new state from
            // the previous cached one and the latest Result emitted from the action processor.
            // The Scan operator is used here for the caching.
            .scan(TripViewState.idle(), reducer)
            // When a reducer just emits previousState, there's no reason to call render. In fact,
            // redrawing the UI in cases like this can cause jank (e.g. messing up snackbar animations
            // by showing the same snackbar twice in rapid succession).
            .distinctUntilChanged()
            // Emit the last one event of the stream on subscription
            // Useful when a View rebinds to the ViewModel after rotation.
            .replay(1)
            // Create the stream on creation without waiting for anyone to subscribe
            // This allows the stream to stay alive even when the UI disconnects and
            // match the stream's lifecycle to the ViewModel's one.
            .autoConnect(0)
    }

    /**
     * Translate an [MviIntent] to an [MviAction].
     * Used to decouple the UI and the business logic to allow easy testings and reusability.
     */
    private fun actionFromIntent(intent: TripIntent): TripAction {
        return when (intent) {
            is TripIntent.InitialIntent -> TripAction.LoadPlacesList(intent.origin, intent.destiny, intent.date)
            else -> TripAction.LoadPlacesList("", "", "")
        }
    }


    override fun processIntents(intents: Observable<TripIntent>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<TripViewState> = statesObservable


    companion object {
        /**
         * The Reducer is where [MviViewState], that the [MviView] will use to
         * render itself, are created.
         * It takes the last cached [MviViewState], the latest [MviResult] and
         * creates a new [MviViewState] by only updating the related fields.
         * This is basically like a big switch statement of all possible types for the [MviResult]
         */
        private val reducer = BiFunction { previousState: TripViewState, result: TripResult ->
            when (result) {
                is TripResult.LoadPlacesResult -> when (result) {

                    is TripResult.LoadPlacesResult.Success -> {

                        previousState.copy(
                            isLoading = false, error = null,
                            viajes = result.places
                        )
                    }

                    is TripResult.LoadPlacesResult.Failure -> previousState.copy(
                        isLoading = false,
                        error = result.error
                    )
                    is TripResult.LoadPlacesResult.InFlight -> previousState.copy(isLoading = true)

                }

                else -> previousState

            }
        }
    }
}
