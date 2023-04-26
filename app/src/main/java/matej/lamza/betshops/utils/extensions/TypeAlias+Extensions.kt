package matej.lamza.betshops.utils.extensions

import matej.lamza.betshops.utils.ScreenDimensions

val ScreenDimensions.width
    get() = this.first

val ScreenDimensions.height
    get() = this.second
