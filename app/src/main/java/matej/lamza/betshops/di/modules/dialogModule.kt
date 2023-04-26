package matej.lamza.betshops.di.modules

import androidx.fragment.app.FragmentManager
import matej.lamza.betshops.common.view.ProgressDialog
import org.koin.dsl.module

val dialogModule = module {
    factory { (manager: FragmentManager) -> ProgressDialog(manager) }
}
