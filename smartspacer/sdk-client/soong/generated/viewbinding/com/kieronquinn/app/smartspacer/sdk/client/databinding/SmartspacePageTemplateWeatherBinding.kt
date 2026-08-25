package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R

class SmartspacePageTemplateWeatherBinding private constructor(
    override val root: android.widget.LinearLayout,
    val smartspaceViewTemplateRoot: android.widget.LinearLayout,
    val smartspacePageTemplateBasicClock: IncludeSmartspacePageClockBinding,
    val smartspacePageTemplateBasicSubtitle: IncludeSmartspacePageSubtitleAndActionBinding,
    val smartspacePageTemplateBasicSupplemental: IncludeSmartspacePageSupplementalBinding
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): SmartspacePageTemplateWeatherBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): SmartspacePageTemplateWeatherBinding {
            val root = inflater.inflate(R.layout.smartspace_page_template_weather, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): SmartspacePageTemplateWeatherBinding {
            val smartspaceViewTemplateRoot = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_view_template_root) ?: throw NullPointerException("Missing required view with ID: smartspace_view_template_root")
            val smartspacePageTemplateBasicClockView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_clock) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_clock")
            val smartspacePageTemplateBasicClock = IncludeSmartspacePageClockBinding.bind(smartspacePageTemplateBasicClockView)
            val smartspacePageTemplateBasicSubtitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_subtitle) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_subtitle")
            val smartspacePageTemplateBasicSubtitle = IncludeSmartspacePageSubtitleAndActionBinding.bind(smartspacePageTemplateBasicSubtitleView)
            val smartspacePageTemplateBasicSupplementalView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_supplemental) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_supplemental")
            val smartspacePageTemplateBasicSupplemental = IncludeSmartspacePageSupplementalBinding.bind(smartspacePageTemplateBasicSupplementalView)
            return SmartspacePageTemplateWeatherBinding(
                rootView as android.widget.LinearLayout,
                smartspaceViewTemplateRoot,
                smartspacePageTemplateBasicClock,
                smartspacePageTemplateBasicSubtitle,
                smartspacePageTemplateBasicSupplemental
            )
        }
    }
}
