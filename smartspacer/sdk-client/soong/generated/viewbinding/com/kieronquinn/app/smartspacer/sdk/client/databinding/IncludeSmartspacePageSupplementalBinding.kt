package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R
import com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView

class IncludeSmartspacePageSupplementalBinding private constructor(
    override val root: android.widget.LinearLayout,
    val smartspacePageSupplementalIcon: com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView,
    val smartspacePageSupplementalText: android.widget.TextView
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): IncludeSmartspacePageSupplementalBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): IncludeSmartspacePageSupplementalBinding {
            val root = inflater.inflate(R.layout.include_smartspace_page_supplemental, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): IncludeSmartspacePageSupplementalBinding {
            val smartspacePageSupplementalIcon = rootView.findViewById<com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView>(R.id.smartspace_page_supplemental_icon) ?: throw NullPointerException("Missing required view with ID: smartspace_page_supplemental_icon")
            val smartspacePageSupplementalText = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_supplemental_text) ?: throw NullPointerException("Missing required view with ID: smartspace_page_supplemental_text")
            return IncludeSmartspacePageSupplementalBinding(
                rootView as android.widget.LinearLayout,
                smartspacePageSupplementalIcon,
                smartspacePageSupplementalText
            )
        }
    }
}
