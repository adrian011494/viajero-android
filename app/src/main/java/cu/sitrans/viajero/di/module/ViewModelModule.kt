package cu.sitrans.viajero.di.module

import androidx.lifecycle.ViewModelProvider
import cu.sitrans.viajero.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ViewModelModule {

//    @Binds
//    @IntoMap
//    @ViewModelKey(InitializeProfileViewModel::class)
//    abstract fun bindInitializeProfileViewModel(initializeProfileViewModel: InitializeProfileViewModel): ViewModel

    @Binds
    @Singleton
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}