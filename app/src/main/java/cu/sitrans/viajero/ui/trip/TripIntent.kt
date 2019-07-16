package cu.sitrans.viajero.ui.trip

import cu.sitrans.viajero.mvi.MviIntent

sealed class TripIntent : MviIntent {
    data class InitialIntent(val origin: String, val destiny: String, val date: String) : TripIntent()
    object RefreshIntent : TripIntent()
}