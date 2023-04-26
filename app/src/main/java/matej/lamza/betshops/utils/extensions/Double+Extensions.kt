package matej.lamza.betshops.utils.extensions

fun Double.round(decimals: Int = 5): Double = "%.${decimals}f".format(this).toDouble()
