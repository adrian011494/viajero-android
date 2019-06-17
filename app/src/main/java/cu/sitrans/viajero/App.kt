package cu.sitrans.viajero

import android.app.Activity
import android.app.Service
import androidx.multidex.MultiDexApplication
import cu.sitrans.viajero.di.component.AppComponent
import cu.sitrans.viajero.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import me.yokeyword.fragmentation.Fragmentation
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class App : MultiDexApplication(), HasActivityInjector, HasServiceInjector {
    private lateinit var appComponent: AppComponent
    var LOG = Timber.tag(this::class.java.simpleName)

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    override fun activityInjector(): AndroidInjector<Activity> = androidInjector

    override fun serviceInjector(): AndroidInjector<Service> = dispatchingServiceInjector


    override fun onCreate() {
        super.onCreate()

        initLog()
        initFragmentation()
        initDagger()

    }


    private fun initFragmentation() {
        //Fragmentation
        Fragmentation.builder()
            .stackViewMode(Fragmentation.BUBBLE)
            .debug(BuildConfig.DEBUG)
            .handleException {
                LOG.e(it)
            }
            .install()
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()

        appComponent.inject(this)
    }

    private fun initLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}