package org.saudigitus.emis.utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.saudigitus.emis.R

object Commons {

    @JvmStatic
    fun alertDialog(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.select_school))
            .setCancelable(true)
            .setMessage(context.getString(R.string.procceed_to_take_attendance_negative_msg))
            .setPositiveButton(context.getString(R.string.action_ok)) { _, _ -> }
            .show()
    }

}