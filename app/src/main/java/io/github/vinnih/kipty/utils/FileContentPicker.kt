package io.github.vinnih.kipty.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun getFileName(context: Context, uri: Uri): String? {
    var name: String? = null

    context.contentResolver.query(uri, null, null, null, null)
        .also { cursor ->
            if (cursor == null) return null
            cursor.use {
                if (it.moveToFirst()) {
                    name = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        .let { index ->
                            if (index != -1)
                                it.getString(index)
                            else null
                        }
                }
            }
        }

    return name
}