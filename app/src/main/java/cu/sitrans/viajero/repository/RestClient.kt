package cu.sitrans.viajero.repository


import cu.sitrans.viajero.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

abstract class RestClient<T>(
    type: Class<T>
) {
    val LOG = Timber.tag(this::class.java.simpleName)


    companion object {

        val LOG_TAG: String = this::class.java.simpleName


        private const val CONNECTION_TIMEOUT_MS = 30000L
        private const val READ_TIMEOUT_MS = 30000L
        private const val WRITE_TIMEOUT_MS = 30000L
    }

    var mApi: T

    // http client
    private var mOkHttpClient: OkHttpClient

    init {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY


        mOkHttpClient = OkHttpClient().newBuilder()
            .apply {
                if (BuildConfig.DEBUG)
                    addInterceptor(logging)
            }
            .connectTimeout(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS)

            .build()

        val retrofit = Retrofit.Builder()
            .client(mOkHttpClient)
            .baseUrl(Constants.SERVER_NAME)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mApi = retrofit.create(type)
    }


}