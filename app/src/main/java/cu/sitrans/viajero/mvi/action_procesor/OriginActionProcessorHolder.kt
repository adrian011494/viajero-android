package cu.sitrans.viajero.mvi.action_procesor

import cu.sitrans.viajero.mvi.ActionProcessorHolder
import cu.sitrans.viajero.ui.base.origin.OriginAction
import cu.sitrans.viajero.ui.base.origin.OriginResult
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class OriginActionProcessorHolder @Inject constructor() : ActionProcessorHolder<OriginAction, OriginResult> {

    override val actionProcessor: ObservableTransformer<OriginAction, OriginResult>
        get() = ObservableTransformer { actions ->
            actions.publish { shared ->

                shared.ofType(OriginAction.LoadPlacesList::class.java).compose(placesProcessor)

                shared.filter { v ->
                    v !is OriginAction.LoadPlacesList

                }.flatMap { w ->
                    Observable.error<OriginResult>(
                        IllegalArgumentException("Unknown Action type: $w")
                    )
                }

            }
        }


    private val placesProcessor =
        ObservableTransformer<OriginAction.LoadPlacesList, OriginResult> { actions ->
            actions.flatMap { action ->

                Observable.just(OriginResult.LoadPlacesResult.Success(listOf()))
                    .cast(OriginResult.LoadPlacesResult::class.java)
                    // Wrap any error into an immutable object and pass it down the stream
                    // without crashing.
                    // Because errors are data and hence, should just be part of the stream.
                    .onErrorReturn(OriginResult.LoadPlacesResult::Failure)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
        }
}