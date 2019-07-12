package cu.sitrans.viajero.repository.model.responce

import cu.sitrans.viajero.repository.model.Localidad

data class LocalidadesResponce(val Localidades: LocalidadesRuta)


data class LocalidadesRuta(val ruta: List<Localidad>)