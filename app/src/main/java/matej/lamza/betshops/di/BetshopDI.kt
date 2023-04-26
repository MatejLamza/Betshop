package matej.lamza.betshops.di

import android.app.Application
import matej.lamza.betshops.di.modules.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module

class BetshopDI(private val application: Application) {
    private lateinit var koinApplication: KoinApplication
    private val modules = listOf<Module>(
        appModule,
        dialogModule,
        networkModule,
        repoModule,
        viewModelModule
    )

    fun init() {
        koinApplication = startKoin {
            androidContext(application)
            modules(modules)
            androidLogger(Level.INFO)
        }
    }
}
