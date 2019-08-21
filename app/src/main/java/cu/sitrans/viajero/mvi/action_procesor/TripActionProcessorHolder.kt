package cu.sitrans.viajero.mvi.action_procesor

import cu.sitrans.viajero.mvi.ActionProcessorHolder
import cu.sitrans.viajero.repository.service.SitransService
import cu.sitrans.viajero.ui.trip.TripAction
import cu.sitrans.viajero.ui.trip.TripResult
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TripActionProcessorHolder @Inject constructor(val sitransService: SitransService) :
    ActionProcessorHolder<TripAction, TripResult> {

    override val actionProcessor: ObservableTransformer<TripAction, TripResult>
        get() = ObservableTransformer { actions ->
            actions.publish { shared ->


                shared.ofType(TripAction.LoadPlacesList::class.java).compose(placesProcessor)
                    .mergeWith(
                        shared.filter { v ->
                            v !is TripAction.LoadPlacesList

                        }.flatMap { w ->
                            Observable.error<TripResult>(
                                IllegalArgumentException("Unknown Action type: $w")
                            )
                        }
                    )


            }
        }


    private val placesProcessor =
        ObservableTransformer<TripAction.LoadPlacesList, TripResult> { actions ->
            actions.flatMap { action ->

                //Original
                /*sitransService.trips(action.origin, action.destiny, action.date, action.dateBack)

                    .concatWith(sitransService.tripsOnlyIda(action.destiny, action.origin, action.dateBack).map {
                        it.map {
                            it.tipo = "Regreso"
                            it
                        }
                    })*/

                    //Edgar ***** Un parche pues en action.dateBack viene con fecha 31/12/69 ademas, concatena Ida con Regreso
                    if (action.date.compareTo(action.dateBack) > 0) {
                        sitransService.tripsOnlyIda(action.origin, action.destiny, action.date)

                            /*.concatWith(sitransService.tripsOnlyIda(action.destiny, action.origin, action.date).map {
                                it.map {
                                    it.tipo = "Ida"
                                    it
                                }
                            })*/
                    }
                    else {
                        sitransService.trips(action.origin, action.destiny, action.date, action.dateBack)

                            /*.concatWith(sitransService.trips(action.destiny, action.origin, action.date, action.dateBack).map {
                                it.map {
                                    it.tipo = "Regreso"
                                    it
                                }
                            })*/
                    }//******

                    .map {
                        TripResult.LoadPlacesResult.Success(it)
                    }
                    .toObservable()
                    .cast(TripResult.LoadPlacesResult::class.java)
                    // Wrap any error into an immutable object and pass it down the stream
                    // without crashing.
                    // Because errors are data and hence, should just be part of the stream.
                    .onErrorReturn(TripResult.LoadPlacesResult::Failure)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
        }
}