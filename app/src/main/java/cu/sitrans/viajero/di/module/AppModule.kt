package cu.sitrans.viajero.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module(includes = [(ViewModelModule::class)])
class AppModule {

    @Provides
    fun providersApplication(app: Application): Context = app

}