package cu.sitrans.viajero.ui.trip

import cu.sitrans.viajero.mvi.MviViewState
import cu.sitrans.viajero.repository.model.Viaje

data class TripViewState(
    val isLoading: Boolean = true,
    val error: Throwable? = null,
    val viajes: List<Viaje> = listOf()
) : MviViewState {


    companion object {
        fun idle(): TripViewState {
            return TripViewState()
        }
    }
}