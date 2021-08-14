package com.amegane3231.imagenotification.data

import android.content.Context
import androidx.annotation.StringRes

data class StringResource(
    @StringRes private val resId: Int,
    private val params: List<Any> = emptyList()
) {
    companion object {
        fun create(
            @StringRes resId: Int,
            vararg params: Any
        ): StringResource {
            return StringResource(resId, listOf(*params))
        }
    }

    fun getString(context: Context): String {
        if (params.isEmpty()) {
            return context.getString(resId)
        }
        return context.getString(resId, *params.toTypedArray())
    }
}