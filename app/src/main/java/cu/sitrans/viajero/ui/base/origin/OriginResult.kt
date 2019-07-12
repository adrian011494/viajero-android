package cu.sitrans.viajero.ui.base.origin

import cu.sitrans.viajero.mvi.MviResult
import cu.sitrans.viajero.repository.model.Localidad

sealed class OriginResult : MviResult {

    sealed class LoadPlacesResult : OriginResult() {
        data class Success(val places: List<Localidad>) : LoadPlacesResult()
        data class Failure(val error: Throwable) : LoadPlacesResult()
        object InFlight : LoadPlacesResult()
    }


}