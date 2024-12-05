package com.sn0w_w0lf.luxevista

object UserSession {
    var userId: Int? = null
    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var phone: String? = null
    var isAdmin: Boolean = false

    var currentReservation: Reservation? = null
    var pastReservations: List<Reservation> = emptyList()

    fun addReservation(reservation: Reservation) {
        currentReservation = reservation
        pastReservations = pastReservations + reservation
    }
    fun clearCurrentReservation() {
        currentReservation = null
    }
}
