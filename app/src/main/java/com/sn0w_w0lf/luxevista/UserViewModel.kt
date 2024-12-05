package com.sn0w_w0lf.luxevista

import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    var userId: Int? = null
    var isAdmin: Boolean = false
    var userName: String? = null
    var userEmail: String? = null

    fun setUserData(id: Int, name: String, email: String, adminStatus: Boolean) {
        userId = id
        userName = name
        userEmail = email
        isAdmin = adminStatus
    }

    fun clearSession() {
        userId = null
        userName = null
        userEmail = null
        isAdmin = false
    }
}
