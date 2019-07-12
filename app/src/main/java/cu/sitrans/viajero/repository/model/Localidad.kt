package cu.sitrans.viajero.repository.model

data class Localidad(val Nombre: String, val Clave: String) {

    override fun toString(): String {
        return Nombre
    }
}