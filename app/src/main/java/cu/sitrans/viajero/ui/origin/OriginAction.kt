package cu.sitrans.viajero.ui.origin

import cu.sitrans.viajero.mvi.MviAction


sealed class OriginAction : MviAction {

    object LoadPlacesList : OriginAction()
    object LoadAgenciasList : OriginAction()

}