package matej.lamza.betshops.utils.extensions

import android.view.View


fun <T : View> T.visibleIf(
    condition: Boolean,
    invisibleStatus: Int = View.GONE,
    apply: T.() -> Unit
) = visibleIf({ condition }, invisibleStatus, apply)


fun <T : View> T.visibleIf(
    condition: () -> Boolean,
    invisibleStatus: Int = View.GONE,
    apply: T.() -> Unit
) {
    if (condition.invoke()) {
        visibility = View.VISIBLE
        apply.invoke(this)
    } else {
        visibility = invisibleStatus
    }
}
