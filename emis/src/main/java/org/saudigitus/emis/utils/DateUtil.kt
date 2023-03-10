package org.saudigitus.emis.utils

import android.annotation.SuppressLint
import org.dhis2.commons.date.DateUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {
    fun formatDate(date: Long): String?  =
        try {
            val formatter = SimpleDateFormat(DateUtils.DATE_FORMAT_EXPRESSION, Locale.US)
            formatter.format(Date(date))
        }catch (e: Exception) {
            Timber.tag("DATE_FORMAT").e(e)
            null
        }

}