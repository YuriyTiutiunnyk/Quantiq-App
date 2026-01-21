package com.example.quantiq.data.notification

import com.example.quantiq.domain.model.NotificationAction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object NotificationJsonAdapter {
    private val gson = Gson()
    private val actionListType = object : TypeToken<List<NotificationAction>>() {}.type

    fun encodeActions(actions: List<NotificationAction>): String =
        gson.toJson(actions, actionListType)

    fun decodeActions(json: String): List<NotificationAction> =
        if (json.isBlank()) emptyList() else gson.fromJson(json, actionListType)
}
