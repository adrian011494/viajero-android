package cu.sitrans.viajero.ui.origin

import cu.sitrans.viajero.mvi.MviIntent

sealed class OriginIntent : MviIntent {
    object InitialIntent : OriginIntent()
    object RefreshIntent : OriginIntent()
    object AgenciasIntent : OriginIntent()
}