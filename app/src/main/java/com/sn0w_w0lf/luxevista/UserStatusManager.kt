package com.sn0w_w0lf.luxevista

import android.content.Context
import android.content.SharedPreferences

class UserStatusManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val USER_STATUS_KEY = "user_status"
    }

    fun setUserStatus(isAdmin: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(USER_STATUS_KEY, isAdmin)
        editor.apply()
    }

    fun getUserStatus(): Boolean {
        return sharedPreferences.getBoolean(USER_STATUS_KEY, false) // Default to false (user)
    }
}
