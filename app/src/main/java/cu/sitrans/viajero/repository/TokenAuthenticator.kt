package cu.sitrans.viajero.repository

import cu.sitrans.viajero.preferences.Preferences
import cu.sitrans.viajero.repository.service.AuthService
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(val preferences: Preferences) : okhttp3.Authenticator {

    val service: AuthService  by lazy {
        AuthService()
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        val (ok, token) = service.newToken()

        if (!ok)
            return null

        preferences.setAuthToken(token)
        // Add new header to rejected request and retry it
        return response.request().newBuilder()
            .header(AUTHORIZATION, token)
            .build()
    }

    companion object {

        const val AUTHORIZATION = "Authorization"
    }
}