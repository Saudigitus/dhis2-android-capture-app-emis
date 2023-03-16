package org.saudigitus.emis.utils

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.WindowManager
import androidx.appcompat.widget.TintTypedArray.obtainStyledAttributes
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

    fun setProgramTheme(activity: Activity, theme: Int) {
        activity.theme.applyStyle(theme, true)

        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val typedValue = TypedValue()
        val a = activity.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorPrimaryDark))
        val colorToReturn = a.getColor(0, 0)
        a.recycle()
        window.statusBarColor = colorToReturn
    }
}