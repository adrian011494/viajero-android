package cu.sitrans.viajero.repository.api

import cu.sitrans.viajero.repository.model.responce.AgenciasResponce
import cu.sitrans.viajero.repository.model.responce.LocalidadesResponce
import cu.sitrans.viajero.repository.model.responce.TripsResponce
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SitransAuthApi {

    @GET("disponibilidad/1.0/localidades")
    fun localidades(): Single<LocalidadesResponce>


    @GET("disponibilidad/1.0/agencias")
    fun agencias(): Single<AgenciasResponce>


    @GET("disponibilidad/1.0/disponibilidad")
    fun trips(
        @Query("origen") origen: String, @Query("destino") destino: String, @Query("fecha_ini") fecha_ini: String
        , @Query("fecha_fin") fecha_fin: String
    ): Single<TripsResponce>

    @GET("disponibilidad/1.0/disponibilidad")

    fun tripsOnlyIda(@Query("origen") origen: String, @Query("destino") destino: String, @Query("fecha_ini") fecha_ini: String): Single<TripsResponce>


}