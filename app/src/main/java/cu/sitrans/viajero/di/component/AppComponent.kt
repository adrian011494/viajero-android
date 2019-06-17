package cu.sitrans.viajero.di.component

import android.app.Application
import cu.sitrans.viajero.App
import cu.sitrans.viajero.di.module.AppModule
import cu.sitrans.viajero.di.module.MainActivityModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        (AppModule::class),
        (AndroidInjectionModule::class)

        , (MainActivityModule::class)

    ]
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)


}