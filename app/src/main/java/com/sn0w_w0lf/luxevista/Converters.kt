package com.sn0w_w0lf.luxevista

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromPreferencesList(preferences: List<String>): String {
        return preferences.joinToString(",")
    }

    @TypeConverter
    fun toPreferencesList(preferences: String): List<String> {
        return preferences.split(",")
    }
}
