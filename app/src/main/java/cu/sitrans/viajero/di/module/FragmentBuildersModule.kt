package cu.sitrans.viajero.di.module

import cu.sitrans.viajero.ui.agencia.AgenciasFragment
import cu.sitrans.viajero.ui.info.InfoFragment
import cu.sitrans.viajero.ui.trip.TripListFragment
import cu.sitrans.viajero.ui.origin.OriginFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeOriginFragment(): OriginFragment

    @ContributesAndroidInjector
    abstract fun contributeTripListFragment(): TripListFragment

    @ContributesAndroidInjector
    abstract fun contributeInfoFragment(): InfoFragment

    @ContributesAndroidInjector
    abstract fun contributeAgenciasFragment(): AgenciasFragment

}