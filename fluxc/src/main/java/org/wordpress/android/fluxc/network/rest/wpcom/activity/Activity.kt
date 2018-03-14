package org.wordpress.android.fluxc.network.rest.wpcom.activity

import java.util.Date

data class Activity(val activityID: String,
                    val summary: String,
                    val text: String,
                    val name: String?,
                    val type: String?,
                    val gridicon: String?,
                    val status: String?,
                    val rewindable: Boolean?,
                    val rewindID: Float?,
                    val published: Date,
                    val isDiscarded: Boolean?,
                    val actor: ActivityActor? = null) {

    enum class Status(value: String) {
        ERROR("error"), SUCCESS("success"), WARNING("warning");
    }

    data class ActivityActor(val displayName: String?,
                             val type: String?,
                             val wpcomUserID: Long?,
                             val avatarURL: String?,
                             val role: String?) {
        val isJetpack = { type == "Application" && displayName == "Jetpack" }
    }
}
