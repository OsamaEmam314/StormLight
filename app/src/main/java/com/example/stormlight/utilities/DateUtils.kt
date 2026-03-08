package com.example.stormlight.utilities
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
object DateUtils {

    fun utcDateLabel(epochSeconds: Long, timezoneOffsetSeconds: Int): String =
        format("yyyy-MM-dd", epochSeconds, timezoneOffsetSeconds)

    fun formatFullDate(epochSeconds: Long, timezoneOffsetSeconds: Int): String =
        format("EEEE, d MMMM", epochSeconds, timezoneOffsetSeconds)

    fun formatTime(epochSeconds: Long, timezoneOffsetSeconds: Int): String =
        format("hh:mm a", epochSeconds, timezoneOffsetSeconds)

    fun formatHourLabel(epochSeconds: Long, timezoneOffsetSeconds: Int): String =
        format("h a", epochSeconds, timezoneOffsetSeconds)

    fun formatDayLabel(epochSeconds: Long, timezoneOffsetSeconds: Int): String =
        format("EEE, MMM d", epochSeconds, timezoneOffsetSeconds)


    private fun format(pattern: String, epochSeconds: Long, timezoneOffsetSeconds: Int): String =
        SimpleDateFormat(pattern, Locale.getDefault())
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .format(Date((epochSeconds + timezoneOffsetSeconds) * 1000L))
}






