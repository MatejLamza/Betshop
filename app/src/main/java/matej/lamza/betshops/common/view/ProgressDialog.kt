package matej.lamza.betshops.common.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import matej.lamza.betshops.databinding.DialogProgressBinding

class ProgressDialog(
    private val manager: FragmentManager,
) : DialogFragment() {

    private lateinit var binding: DialogProgressBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setupUI()
    }


    fun show(tag: String = "progress") {
        if (!isVisible) {
            show(manager, tag)
        }
    }
}
