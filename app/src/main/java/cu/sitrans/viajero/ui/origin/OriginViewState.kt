package cu.sitrans.viajero.ui.origin

import cu.sitrans.viajero.mvi.MviViewState
import cu.sitrans.viajero.repository.model.Contacto
import cu.sitrans.viajero.repository.model.Localidad

data class OriginViewState(
    val isLoading: Boolean = true,
    val error: Throwable? = null,
    val localidades: List<Localidad> = listOf(),
    val agencias: List<Contacto> = listOf()
) : MviViewState {


    companion object {
        fun idle(): OriginViewState {
            return OriginViewState()
        }
    }
}