package cu.sitrans.viajero.repository.service

import cu.sitrans.viajero.repository.api.SitransAuthApi
import javax.inject.Inject

class SitransService @Inject constructor(val api: SitransAuthApi) {


    fun localidades() = api.localidades()
        .map {
            it.Localidades.ruta
        }


    fun agencias() = api.agencias()
        .map {
            it.entries.entry
        }


    fun trips(origin: String, destiny: String, date: String, dateBack: String?) = api.trips(
        origin, destiny, date, dateBack ?: ""
    ).map {
        it.disponibilidad.viaje
    }

    fun tripsOnlyIda(origin: String, destiny: String, date: String) = api.tripsOnlyIda(
        origin, destiny, date
    ).map {
        it.disponibilidad.viaje
    }
}