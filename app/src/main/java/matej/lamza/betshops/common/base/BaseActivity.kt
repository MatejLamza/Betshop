package matej.lamza.betshops.common.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import matej.lamza.betshops.utils.ActivityViewBindingInflate

abstract class BaseActivity<VB : ViewBinding>(private val inflate: ActivityViewBindingInflate<VB>? = null) :
    AppCompatActivity(), View {

    private var _binding: VB? = null
    val binding: VB
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflate?.invoke(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
