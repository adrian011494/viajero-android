package cu.sitrans.viajero.ui.base.origin

import cu.sitrans.viajero.mvi.MviResult

sealed class OriginResult : MviResult {

    sealed class LoadPlacesResult : OriginResult() {
        data class Success(val places: List<String>) : LoadPlacesResult()
        data class Failure(val error: Throwable) : LoadPlacesResult()
        object InFlight : LoadPlacesResult()
    }


}