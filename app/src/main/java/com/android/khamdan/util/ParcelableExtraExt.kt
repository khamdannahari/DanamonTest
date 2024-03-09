package com.android.khamdan.util

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable

object ParcelableExtraExt {

    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

}