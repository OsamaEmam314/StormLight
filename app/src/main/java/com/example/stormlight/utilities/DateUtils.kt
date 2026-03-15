package com.example.stormlight.utilities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    // "2024-10-23"
    fun utcDateLabel(epochSeconds: Long, timezoneOffsetSeconds: Int): String {
        val localEpoch = (epochSeconds + timezoneOffsetSeconds) * 1000L
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(localEpoch))
    }


    // "Monday, 9 March" (EN) or "الاثنين، 9 مارس" (AR).
    fun formatFullDate(
        epochSeconds: Long,
        timezoneOffsetSeconds: Int,
        locale: Locale = Locale.ENGLISH
    ): String {
        val localEpoch = (epochSeconds + timezoneOffsetSeconds) * 1000L
        val sdf = SimpleDateFormat("EEEE, d MMMM", locale)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(localEpoch))
    }

    // "03 PM" (EN) or "03 م" (AR).
    fun formatHourLabel(
        epochSeconds: Long,
        timezoneOffsetSeconds: Int,
        locale: Locale = Locale.ENGLISH
    ): String {
        val localEpoch = (epochSeconds + timezoneOffsetSeconds) * 1000L
        val sdf = SimpleDateFormat("hh a", locale)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(localEpoch)).uppercase(locale)
    }

    // "Mon" (EN) or "الاثنين" (AR).
    fun formatDayLabel(
        epochSeconds: Long,
        timezoneOffsetSeconds: Int,
        locale: Locale = Locale.ENGLISH
    ): String {
        val localEpoch = (epochSeconds + timezoneOffsetSeconds) * 1000L
        val sdf = SimpleDateFormat("EEE", locale)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(localEpoch))
    }

    // "Oct 23" (EN) or "23 أكتوبر" (AR).
    fun formatShortDate(
        epochSeconds: Long,
        timezoneOffsetSeconds: Int,
        locale: Locale = Locale.ENGLISH
    ): String {
        val localEpoch = (epochSeconds + timezoneOffsetSeconds) * 1000L
        val pattern = if (locale.language == "ar") "d MMM" else "MMM d"
        val sdf = SimpleDateFormat(pattern, locale)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(localEpoch))
    }

    fun formatAlertTime(hour: Int, minute: Int): String {
        val h = if (hour % 12 == 0) 12 else hour % 12
        val m = minute.toString().padStart(2, '0')
        val amPm = if (hour < 12) "AM" else "PM"
        return "$h:$m $amPm"
    }
}