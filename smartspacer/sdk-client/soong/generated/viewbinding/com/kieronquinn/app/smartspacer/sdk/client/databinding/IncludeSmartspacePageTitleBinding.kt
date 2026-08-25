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

class IncludeSmartspacePageTitleBinding private constructor(
    override val root: androidx.constraintlayout.widget.ConstraintLayout,
    val smartspaceViewTitle: android.widget.TextView,
    val smartspaceViewPrimaryActionContainer: android.widget.LinearLayout,
    val smartspaceViewPrimaryActionIcon: com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView,
    val smartspaceViewPrimaryActionText: android.widget.TextView
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): IncludeSmartspacePageTitleBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): IncludeSmartspacePageTitleBinding {
            val root = inflater.inflate(R.layout.include_smartspace_page_title, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): IncludeSmartspacePageTitleBinding {
            val smartspaceViewTitle = rootView.findViewById<android.widget.TextView>(R.id.smartspace_view_title) ?: throw NullPointerException("Missing required view with ID: smartspace_view_title")
            val smartspaceViewPrimaryActionContainer = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_view_primary_action_container) ?: throw NullPointerException("Missing required view with ID: smartspace_view_primary_action_container")
            val smartspaceViewPrimaryActionIcon = rootView.findViewById<com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView>(R.id.smartspace_view_primary_action_icon) ?: throw NullPointerException("Missing required view with ID: smartspace_view_primary_action_icon")
            val smartspaceViewPrimaryActionText = rootView.findViewById<android.widget.TextView>(R.id.smartspace_view_primary_action_text) ?: throw NullPointerException("Missing required view with ID: smartspace_view_primary_action_text")
            return IncludeSmartspacePageTitleBinding(
                rootView as androidx.constraintlayout.widget.ConstraintLayout,
                smartspaceViewTitle,
                smartspaceViewPrimaryActionContainer,
                smartspaceViewPrimaryActionIcon,
                smartspaceViewPrimaryActionText
            )
        }
    }
}
