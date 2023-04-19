package matej.lamza.betshops.di.modules

import matej.lamza.betshops.ui.map.MapViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { MapViewModel(get()) }
}
