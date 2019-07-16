package cu.sitrans.viajero.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cu.sitrans.viajero.ui.trip.TripListViewModel
import cu.sitrans.viajero.ui.origin.OriginViewModel
import cu.sitrans.viajero.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(OriginViewModel::class)
    abstract fun bindOriginViewModel(originViewModel: OriginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TripListViewModel::class)
    abstract fun bindTripListViewModel(tripListViewModel: TripListViewModel): ViewModel

    @Binds
    @Singleton
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}