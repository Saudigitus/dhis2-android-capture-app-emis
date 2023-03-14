package org.saudigitus.emis.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterSettings(
    val ou: String,
    val program: String
) : Parcelable
