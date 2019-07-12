package cu.sitrans.viajero.ui.base.trip

import cu.sitrans.viajero.mvi.MviResult
import cu.sitrans.viajero.repository.model.Localidad
import cu.sitrans.viajero.repository.model.Viaje

sealed class TripResult : MviResult {

    sealed class LoadPlacesResult : TripResult() {
        data class Success(val places: List<Viaje>) : LoadPlacesResult()
        data class Failure(val error: Throwable) : LoadPlacesResult()
        object InFlight : LoadPlacesResult()
    }
}