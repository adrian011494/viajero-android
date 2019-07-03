package cu.sitrans.viajero.ui.base.origin

import cu.sitrans.viajero.mvi.MviAction


sealed class OriginAction : MviAction {

    object LoadPlacesList : OriginAction()

}