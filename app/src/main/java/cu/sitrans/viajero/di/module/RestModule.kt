package cu.sitrans.viajero.di.module

import com.google.gson.GsonBuilder
import cu.sitrans.viajero.BuildConfig
import cu.sitrans.viajero.preferences.Preferences
import cu.sitrans.viajero.repository.Constants
import cu.sitrans.viajero.repository.api.SitransAuthApi
import cu.sitrans.viajero.repository.service.SitransService

import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class RestModule {

    @Singleton
    @Provides
    fun providerOkHttpBuilder(): OkHttpClient.Builder {
        return OkHttpClient().newBuilder()
//            .apply {
//                if (BuildConfig.DEBUG)
//                    addInterceptor(
//                        HttpLoggingInterceptor()
//                            .apply {
//                                level = HttpLoggingInterceptor.Level.BODY
//                            })
//            }
            .connectTimeout(45000L, TimeUnit.MILLISECONDS)
            .readTimeout(45000L, TimeUnit.MILLISECONDS)
            .writeTimeout(45000L, TimeUnit.MILLISECONDS)
    }

    @Singleton
    @Provides
    fun providerRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
    }

    @Singleton
    @Provides
    @Named("authRetrofit")
    fun providerAuthRetrofit(
        retrofitBuilder: Retrofit.Builder,
        okHttpClientBuilder: OkHttpClient.Builder,
        preferences: Preferences
    ): Retrofit {
        val client = okHttpClientBuilder.apply {
            addInterceptor(Interceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    //.header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                requestBuilder.header(
                    "Authorization",
                    "Bearer ${preferences.getAuthToken()}"
                )

                Timber.w("Authorization Bearer ${preferences.getAuthToken()}")
                return@Interceptor chain.proceed(requestBuilder.build())
            })


            if (BuildConfig.DEBUG)
                addInterceptor(
                    HttpLoggingInterceptor()
                        .apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        })


        }.build()

        return retrofitBuilder
            .client(client)
            .baseUrl(Constants.SERVER_NAME)
            .build()
    }

    @Singleton
    @Provides
    @Named("retrofit")
    fun providerBaseRetrofit(
        retrofitBuilder: Retrofit.Builder,
        okHttpClientBuilder: OkHttpClient.Builder
    ): Retrofit {

        val client = okHttpClientBuilder.apply {
            addInterceptor(Interceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                return@Interceptor chain.proceed(requestBuilder.build())
            })
        }.build()

        return retrofitBuilder
            .client(client)
            .baseUrl(Constants.SERVER_NAME)
            .build()
    }

    /* @Singleton
     @Provides
     fun providerServices(@Named("retrofit") retrofit: Retrofit): ApklisApi {
         return retrofit.create(SitransAuthApi::class.java)
     }*/

    @Singleton
    @Provides
    fun providerServices(@Named("authRetrofit") retrofit: Retrofit): SitransAuthApi {
        return retrofit.create(SitransAuthApi::class.java)
    }

}