package matej.lamza.betshops.common.base

interface View {
    fun dismissLoading()

    fun showLoading()

    fun showError(error: Throwable)
}
