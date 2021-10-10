package com.example.loanmonitoring.models

import java.util.*

class UserModel(
    var uid: String = "",
    var displayName: String = "",
    var email: String = "",
    var createdOn: Calendar? = null
) {
    constructor(map: HashMap<*, *>) : this(
        uid = map["uid"].toString(),
        displayName = map["displayName"].toString(),
        email = map["email"].toString()
    ) {
        // Check if createdOn is set and if it does, convert it to a Calendar object
        if (map.containsKey("createdOn") && map["createdOn"] != null) {
            Calendar.getInstance().let { calendar ->
                calendar.timeInMillis = map["createdOn"] as Long
                this.createdOn = calendar
            }
        }
    }

    fun toMap(): HashMap<String, Any> {
        val user = HashMap<String, Any>()

        user["uid"] = this.uid
        user["displayName"] = this.displayName
        user["email"] = this.email
        this.createdOn?.let { calendar -> user["createdOn"] = calendar.timeInMillis }

        return user
    }
}