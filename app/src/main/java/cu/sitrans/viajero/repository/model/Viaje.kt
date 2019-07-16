package cu.sitrans.viajero.repository.model

data class Viaje(
    val capac: String?,
    val clima: String?,
    val denominacion: String?,
    val fecha: String?,
    val fecha_llegada: String?,
    val medio: String?,
    val precio: String?,
    var tipo: String?,
    val via: String?
)