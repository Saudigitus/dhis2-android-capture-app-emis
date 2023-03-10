package org.saudigitus.emis.data.model

import android.os.Parcelable
import java.util.LinkedHashMap
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue

@Parcelize
data class TeiModel(
    val attrs: @RawValue LinkedHashMap<String, TrackedEntityAttributeValue>
) : Parcelable
