package cu.sitrans.viajero.mvi.action_procesor

import cu.sitrans.viajero.mvi.ActionProcessorHolder
import cu.sitrans.viajero.repository.service.SitransService
import cu.sitrans.viajero.ui.origin.OriginAction
import cu.sitrans.viajero.ui.origin.OriginResult
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class OriginActionProcessorHolder @Inject constructor(val sitransService: SitransService) :
    ActionProcessorHolder<OriginAction, OriginResult> {

    override val actionProcessor: ObservableTransformer<OriginAction, OriginResult>
        get() = ObservableTransformer { actions ->
            actions.publish { shared ->

                Observable.merge(
                    shared.ofType(OriginAction.LoadAgenciasList::class.java).compose(agenciasProcessor),
                    shared.ofType(OriginAction.LoadPlacesList::class.java).compose(placesProcessor)
                        .mergeWith(
                            shared.filter { v ->
                                v !is OriginAction.LoadPlacesList
                                        && v !is OriginAction.LoadAgenciasList

                            }.flatMap { w ->
                                Observable.error<OriginResult>(
                                    IllegalArgumentException("Unknown Action type: $w")
                                )
                            }
                        )
                )


            }
        }


    private val agenciasProcessor =
        ObservableTransformer<OriginAction.LoadAgenciasList, OriginResult> { actions ->
            actions.flatMap { action ->

                sitransService.agencias()
                    .map {
                        OriginResult.LoadAgenciasResult.Success(it)
                    }
                    .toObservable()
                    .cast(OriginResult.LoadAgenciasResult::class.java)
                    // Wrap any error into an immutable object and pass it down the stream
                    // without crashing.
                    // Because errors are data and hence, should just be part of the stream.
                    .onErrorReturn(OriginResult.LoadAgenciasResult::Failure)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
        }

    private val placesProcessor =
        ObservableTransformer<OriginAction.LoadPlacesList, OriginResult> { actions ->
            actions.flatMap { action ->

                sitransService.localidades()
                    .map {
                        OriginResult.LoadPlacesResult.Success(it)
                    }
                    .toObservable()
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