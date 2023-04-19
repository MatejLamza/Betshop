package matej.lamza.betshops

import android.app.Application
import matej.lamza.betshops.di.BetshopDI

class BetshopApplication : Application() {

    private val betshopDI: BetshopDI by lazy { BetshopDI(this) }

    override fun onCreate() {
        super.onCreate()
        betshopDI.init()
    }
}
