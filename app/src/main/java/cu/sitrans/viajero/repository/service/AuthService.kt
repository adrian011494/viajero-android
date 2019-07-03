package cu.sitrans.viajero.repository.service

import cu.sitrans.viajero.repository.RestClient
import cu.sitrans.viajero.repository.api.SitransAuthApi

class AuthService : RestClient<SitransAuthApi>(SitransAuthApi::class.java) {

    fun newToken() = false to ""
}