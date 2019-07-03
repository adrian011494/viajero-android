package cu.sitrans.viajero.ui.base.origin

import cu.sitrans.viajero.mvi.MviViewState

data class OriginViewState(
    val isLoading: Boolean = true,
    val error: Throwable? = null
) : MviViewState {


    companion object {
        fun idle(): OriginViewState {
            return OriginViewState()
        }
    }
}