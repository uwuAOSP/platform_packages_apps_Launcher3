package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R
import com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView

class IncludeSmartspacePageSubtitleBinding private constructor(
    override val root: android.widget.LinearLayout,
    val smartspacePageSubtitleIcon: com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView,
    val smartspacePageSubtitleText: android.widget.TextView
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): IncludeSmartspacePageSubtitleBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): IncludeSmartspacePageSubtitleBinding {
            val root = inflater.inflate(R.layout.include_smartspace_page_subtitle, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): IncludeSmartspacePageSubtitleBinding {
            val smartspacePageSubtitleIcon = rootView.findViewById<com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView>(R.id.smartspace_page_subtitle_icon) ?: throw NullPointerException("Missing required view with ID: smartspace_page_subtitle_icon")
            val smartspacePageSubtitleText = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_subtitle_text) ?: throw NullPointerException("Missing required view with ID: smartspace_page_subtitle_text")
            return IncludeSmartspacePageSubtitleBinding(
                rootView as android.widget.LinearLayout,
                smartspacePageSubtitleIcon,
                smartspacePageSubtitleText
            )
        }
    }
}
