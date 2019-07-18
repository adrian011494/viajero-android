package cu.sitrans.viajero.preferences

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(app: Application) {

    companion object {
        private const val SHARED_PREF_NAME = "sitras_shared_preferences"
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"

    }

    private val sharedPreferences by lazy(LazyThreadSafetyMode.NONE) {
        app.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }


    fun getAuthToken(): String {
        return sharedPreferences.getString(ACCESS_TOKEN, "4f3fc3a8-14c0-38d3-a122-fef22c3e00fb") ?: ""
    }

    fun setAuthToken(token:String) {
         sharedPreferences.put {
            putString(ACCESS_TOKEN,token)
        }
    }


}

private inline fun SharedPreferences.put(body: SharedPreferences.Editor.() -> Unit) {
    val editor = this.edit()
    editor.body()
    editor.apply()
    editor.commit()
}