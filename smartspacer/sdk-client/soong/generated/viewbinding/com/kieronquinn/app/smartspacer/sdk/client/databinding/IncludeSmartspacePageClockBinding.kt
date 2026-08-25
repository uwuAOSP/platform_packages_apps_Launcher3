package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextClock
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R
import com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView

class IncludeSmartspacePageClockBinding private constructor(
    override val root: androidx.constraintlayout.widget.ConstraintLayout,
    val smartspaceViewTitle: android.widget.TextClock,
    val smartspacePagePrimaryActionContainer: android.widget.LinearLayout,
    val smartspacePagePrimaryActionIcon: com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView,
    val smartspacePagePrimaryActionText: android.widget.TextView
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): IncludeSmartspacePageClockBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): IncludeSmartspacePageClockBinding {
            val root = inflater.inflate(R.layout.include_smartspace_page_clock, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): IncludeSmartspacePageClockBinding {
            val smartspaceViewTitle = rootView.findViewById<android.widget.TextClock>(R.id.smartspace_view_title) ?: throw NullPointerException("Missing required view with ID: smartspace_view_title")
            val smartspacePagePrimaryActionContainer = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_primary_action_container) ?: throw NullPointerException("Missing required view with ID: smartspace_page_primary_action_container")
            val smartspacePagePrimaryActionIcon = rootView.findViewById<com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView>(R.id.smartspace_page_primary_action_icon) ?: throw NullPointerException("Missing required view with ID: smartspace_page_primary_action_icon")
            val smartspacePagePrimaryActionText = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_primary_action_text) ?: throw NullPointerException("Missing required view with ID: smartspace_page_primary_action_text")
            return IncludeSmartspacePageClockBinding(
                rootView as androidx.constraintlayout.widget.ConstraintLayout,
                smartspaceViewTitle,
                smartspacePagePrimaryActionContainer,
                smartspacePagePrimaryActionIcon,
                smartspacePagePrimaryActionText
            )
        }
    }
}
