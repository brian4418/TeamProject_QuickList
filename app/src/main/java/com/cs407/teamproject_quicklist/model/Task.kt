package com.cs407.teamproject_quicklist.model

import com.google.firebase.database.Exclude
import java.io.Serializable

data class Task(
    var id: String = "",
    val title: String = "",
    val deadline: String = "",
    val description: String = "",
    val priority: String = "",
    val recurring: String = "",
    var isComplete: Boolean = false
) : Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "title" to title,
            "deadline" to deadline,
            "description" to description,
            "priority" to priority,
            "recurring" to recurring,
            "isComplete" to isComplete
        )
    }
}
