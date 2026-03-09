package com.example.stormlight.utilities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    /**
     * Returns a stable date key used for grouping forecast items by day.
     * Always English + UTC-adjusted — this is a grouping key, NOT displayed text.
     * e.g. "2024-10-23"
     */
    fun utcDateLabel(epochSeconds: Long, timezoneOffsetSeconds: Int): String {
        val localEpoch = (epochSeconds + timezoneOffsetSeconds) * 1000L
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(localEpoch))
    }

    /**
     * Full date — e.g. "Monday, 9 March" (EN) or "الاثنين، 9 مارس" (AR).
     * Locale-aware.
     */
    fun formatFullDate(epochSeconds: Long, timezoneOffsetSeconds: Int, locale: Locale = Locale.ENGLISH): String {
        val localEpoch = (epochSeconds + timezoneOffsetSeconds) * 1000L
        val sdf = SimpleDateFormat("EEEE, d MMMM", locale)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(localEpoch))
    }

    /**
     * Time — e.g. "03:31 PM" (EN) or "03:31 م" (AR).
     * Locale-aware.
     */
    fun formatTime(epochSeconds: Long, timezoneOffsetSeconds: Int, locale: Locale = Locale.ENGLISH): String {
        val localEpoch = (epochSeconds + timezoneOffsetSeconds) * 1000L
        val sdf = SimpleDateFormat("hh:mm a", locale)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(localEpoch))
    }

    /**
     * Short hour label for HourlyForecast cards — e.g. "03 PM" (EN) or "03 م" (AR).
     * Locale-aware.
     */
    fun formatHourLabel(epochSeconds: Long, timezoneOffsetSeconds: Int, locale: Locale = Locale.ENGLISH): String {
        val localEpoch = (epochSeconds + timezoneOffsetSeconds) * 1000L
        val sdf = SimpleDateFormat("hh a", locale)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(localEpoch)).uppercase(locale)
    }

    /**
     * Short day name for DailyForecast rows — e.g. "Mon" (EN) or "الاثنين" (AR).
     * Locale-aware.
     */
    fun formatDayLabel(epochSeconds: Long, timezoneOffsetSeconds: Int, locale: Locale = Locale.ENGLISH): String {
        val localEpoch = (epochSeconds + timezoneOffsetSeconds) * 1000L
        val sdf = SimpleDateFormat("EEE", locale)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(localEpoch))
    }

    /**
     * Short month+day for DailyForecast subtitle — e.g. "Oct 23" (EN) or "23 أكتوبر" (AR).
     * Locale-aware — pattern flips for Arabic to match natural reading order.
     */
    fun formatShortDate(epochSeconds: Long, timezoneOffsetSeconds: Int, locale: Locale = Locale.ENGLISH): String {
        val localEpoch = (epochSeconds + timezoneOffsetSeconds) * 1000L
        // Arabic reads day-first naturally: "23 أكتوبر"
        val pattern = if (locale.language == "ar") "d MMM" else "MMM d"
        val sdf = SimpleDateFormat(pattern, locale)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(localEpoch))
    }
}