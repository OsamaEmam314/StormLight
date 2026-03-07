package com.example.stormlight.utilities

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.stormlight.utilities.enums.Language

object LocaleUtils {
    fun applyLocale(language: Language, context: Context){
        val localeList = LocaleListCompat.forLanguageTags(language.language)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(language.language)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.language))
        }
    }
}