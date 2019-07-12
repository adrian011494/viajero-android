package cu.sitrans.viajero.repository.service

import cu.sitrans.viajero.repository.api.SitransAuthApi
import javax.inject.Inject

class SitransService @Inject constructor(val api: SitransAuthApi) {


    fun localidades() = api.localidades()
        .map {
            it.Localidades.ruta
        }


    fun trips(origin: String, destiny: String, date: String) = api.trips(
        origin, destiny, date
    ).map {
        it.disponibilidad.viaje
    }
}