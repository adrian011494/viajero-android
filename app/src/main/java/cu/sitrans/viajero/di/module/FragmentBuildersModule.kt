package cu.sitrans.viajero.di.module

import cu.sitrans.viajero.ui.base.Trip.TripListFragment
import cu.sitrans.viajero.ui.base.origin.OriginFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeOriginFragment(): OriginFragment

    @ContributesAndroidInjector
    abstract fun contributeTripListFragment(): TripListFragment

}