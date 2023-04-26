package matej.lamza.betshops.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*


object DateUtils {

    const val START_TIME = 8
    const val END_TIME = 16

    fun isCurrentlyOpened(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            isCurrentTimeInBetweenWorkingHoursAPI26()
        } else {
            isCurrentTimeInBetweenWorkingHours()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isCurrentTimeInBetweenWorkingHoursAPI26(
        startHours: Int = 8,
        startMinutes: Int = 0,
        endHours: Int = 16,
        endMinutes: Int = 0
    ): Boolean {
        val currentTime = LocalTime.now()
        val start = LocalTime.of(startHours, startMinutes)
        val end = LocalTime.of(endHours, endMinutes)

        return currentTime in start..end
    }

    private fun isCurrentTimeInBetweenWorkingHours(
        startHours: Int = 8,
        startMinutes: Int = 0,
        endHours: Int = 16,
        endMinutes: Int = 0
    ): Boolean {
        val currentTime = Date()
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        val start = Calendar.getInstance()
        start.set(Calendar.HOUR_OF_DAY, startHours)
        start.set(Calendar.MINUTE, startMinutes)

        val end = Calendar.getInstance()
        end.set(Calendar.HOUR_OF_DAY, endHours)
        end.set(Calendar.MINUTE, endMinutes)

        return (currentTime.after(start.time) && currentTime.before(end.time))
    }


}
