package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R

class SmartspaceLongPressPopupBinding private constructor(
    override val root: android.widget.LinearLayout,
    val smartspaceLongPressPopupDismiss: android.widget.Button,
    val smartspaceLongPressPopupAbout: android.widget.Button,
    val smartspaceLongPressPopupFeedback: android.widget.Button,
    val smartspaceLongPressPopupSettings: android.widget.Button
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): SmartspaceLongPressPopupBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): SmartspaceLongPressPopupBinding {
            val root = inflater.inflate(R.layout.smartspace_long_press_popup, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): SmartspaceLongPressPopupBinding {
            val smartspaceLongPressPopupDismiss = rootView.findViewById<android.widget.Button>(R.id.smartspace_long_press_popup_dismiss) ?: throw NullPointerException("Missing required view with ID: smartspace_long_press_popup_dismiss")
            val smartspaceLongPressPopupAbout = rootView.findViewById<android.widget.Button>(R.id.smartspace_long_press_popup_about) ?: throw NullPointerException("Missing required view with ID: smartspace_long_press_popup_about")
            val smartspaceLongPressPopupFeedback = rootView.findViewById<android.widget.Button>(R.id.smartspace_long_press_popup_feedback) ?: throw NullPointerException("Missing required view with ID: smartspace_long_press_popup_feedback")
            val smartspaceLongPressPopupSettings = rootView.findViewById<android.widget.Button>(R.id.smartspace_long_press_popup_settings) ?: throw NullPointerException("Missing required view with ID: smartspace_long_press_popup_settings")
            return SmartspaceLongPressPopupBinding(
                rootView as android.widget.LinearLayout,
                smartspaceLongPressPopupDismiss,
                smartspaceLongPressPopupAbout,
                smartspaceLongPressPopupFeedback,
                smartspaceLongPressPopupSettings
            )
        }
    }
}
