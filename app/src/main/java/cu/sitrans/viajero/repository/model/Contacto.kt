package cu.sitrans.viajero.repository.model

import ir.mirrajabi.searchdialog.core.Searchable

data class Contacto(
    val coordenadas: String?,
    val direccion: String?,
    val id: String,
    val nombre: String,
    val telefono: String?
) : Searchable {
    override fun getTitle(): String {
        return nombre
    }
}