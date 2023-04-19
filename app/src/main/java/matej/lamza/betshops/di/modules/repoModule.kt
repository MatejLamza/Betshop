package matej.lamza.betshops.di.modules

import matej.lamza.betshops.data.BetshopLocationsRepo
import matej.lamza.betshops.data.repository.BetshopLocationRepoImpl
import org.koin.dsl.module

val repoModule = module {
    single<BetshopLocationsRepo> { BetshopLocationRepoImpl(betshopAPI = get()) }
}
