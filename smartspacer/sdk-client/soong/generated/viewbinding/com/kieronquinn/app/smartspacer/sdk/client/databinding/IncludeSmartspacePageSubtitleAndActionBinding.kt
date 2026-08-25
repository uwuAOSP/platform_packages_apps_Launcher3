package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R
import com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView

class IncludeSmartspacePageSubtitleAndActionBinding private constructor(
    override val root: androidx.constraintlayout.widget.ConstraintLayout,
    val smartspacePageSubtitleContainer: android.widget.LinearLayout,
    val smartspacePageSubtitleIcon: com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView,
    val smartspacePageSubtitleText: android.widget.TextView,
    val smartspacePageActionContainer: android.widget.LinearLayout,
    val smartspacePageActionIcon: com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView,
    val smartspacePageActionText: android.widget.TextView
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): IncludeSmartspacePageSubtitleAndActionBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): IncludeSmartspacePageSubtitleAndActionBinding {
            val root = inflater.inflate(R.layout.include_smartspace_page_subtitle_and_action, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): IncludeSmartspacePageSubtitleAndActionBinding {
            val smartspacePageSubtitleContainer = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_subtitle_container) ?: throw NullPointerException("Missing required view with ID: smartspace_page_subtitle_container")
            val smartspacePageSubtitleIcon = rootView.findViewById<com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView>(R.id.smartspace_page_subtitle_icon) ?: throw NullPointerException("Missing required view with ID: smartspace_page_subtitle_icon")
            val smartspacePageSubtitleText = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_subtitle_text) ?: throw NullPointerException("Missing required view with ID: smartspace_page_subtitle_text")
            val smartspacePageActionContainer = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_action_container) ?: throw NullPointerException("Missing required view with ID: smartspace_page_action_container")
            val smartspacePageActionIcon = rootView.findViewById<com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView>(R.id.smartspace_page_action_icon) ?: throw NullPointerException("Missing required view with ID: smartspace_page_action_icon")
            val smartspacePageActionText = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_action_text) ?: throw NullPointerException("Missing required view with ID: smartspace_page_action_text")
            return IncludeSmartspacePageSubtitleAndActionBinding(
                rootView as androidx.constraintlayout.widget.ConstraintLayout,
                smartspacePageSubtitleContainer,
                smartspacePageSubtitleIcon,
                smartspacePageSubtitleText,
                smartspacePageActionContainer,
                smartspacePageActionIcon,
                smartspacePageActionText
            )
        }
    }
}
