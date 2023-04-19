package matej.lamza.betshops.ui.map

import android.os.Bundle
import matej.lamza.betshops.common.base.BaseActivity
import matej.lamza.betshops.databinding.ActivityMapsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapActivity : BaseActivity<ActivityMapsBinding>(ActivityMapsBinding::inflate) {

    private val mapViewModel by viewModel<MapViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapViewModel
    }

}
