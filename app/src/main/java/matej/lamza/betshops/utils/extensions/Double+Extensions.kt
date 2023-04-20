package matej.lamza.betshops.utils.extensions

fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()
