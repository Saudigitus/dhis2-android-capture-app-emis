package org.saudigitus.emis.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import java.util.LinkedHashMap

@Parcelize
data class TeiModel(
    val attrs: @RawValue LinkedHashMap<String, TrackedEntityAttributeValue>
) : Parcelable
