package com.divyanshu.doctorapp

/**
 * In-memory singleton to hold the authenticated user's session data.
 * Populated on login, cleared on logout.
 */
object TokenManager {
    var token: String = ""
    var userId: Int = 0
    var userRole: String = ""
}
