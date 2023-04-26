package matej.lamza.betshops.common.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import matej.lamza.betshops.R
import matej.lamza.betshops.common.Available
import matej.lamza.betshops.common.ConnectionState
import matej.lamza.betshops.common.Unavailable
import matej.lamza.betshops.common.view.ProgressDialog
import matej.lamza.betshops.utils.ActivityViewBindingInflate
import matej.lamza.betshops.utils.extensions.errorSnackBar
import matej.lamza.betshops.utils.extensions.infoSnackBar
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.UnknownHostException

abstract class BaseActivity<VB : ViewBinding>(private val inflate: ActivityViewBindingInflate<VB>? = null) :
    AppCompatActivity(), View {

    private var _binding: VB? = null
    val binding: VB
        get() = _binding!!

    protected val progressDialog: ProgressDialog by inject { parametersOf(supportFragmentManager, "Loading") }

    private val connectivityManager by lazy {
        kotlin.runCatching {
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }.getOrNull()
    }

    private val request = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    private val networkStatus = callbackFlow<ConnectionState> {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onUnavailable() {
                trySend(Unavailable)
            }

            override fun onAvailable(network: Network) {
                trySend(Available)
            }

            override fun onLost(network: Network) {
                trySend(Unavailable)
            }
        }

        connectivityManager?.registerNetworkCallback(request, networkCallback)

        awaitClose { connectivityManager?.unregisterNetworkCallback(networkCallback) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflate?.invoke(layoutInflater)
        setContentView(binding.root)

        networkStatus.asLiveData().observe(this) {
            when (it) {
                Available -> onNetworkAvailable()
                Unavailable -> onNetworkLost()
            }
        }
    }

    open fun onNetworkLost() {
        errorSnackBar(binding.root, getString(R.string.connection_lost)).show()

    }

    open fun onNetworkAvailable() {
        infoSnackBar(binding.root, getString(R.string.connection_restored)).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun dismissLoading() {
        progressDialog.dismiss()
    }

    override fun showLoading() {
        progressDialog.show()
    }

    //Not the way to handle errors!
    override fun showError(error: Throwable) {
        if (error !is UnknownHostException)
            errorSnackBar(binding.root, "Something went wrong! ${error.message}").show()
    }
}
