package cu.sitrans.viajero.ui.base.trip

import cu.sitrans.viajero.mvi.MviAction


sealed class TripAction : MviAction {

    data class LoadPlacesList(val origin: String, val destiny: String, val date: String) : TripAction()

}