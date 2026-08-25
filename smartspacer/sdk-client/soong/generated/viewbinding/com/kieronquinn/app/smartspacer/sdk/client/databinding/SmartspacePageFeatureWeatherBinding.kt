package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R

class SmartspacePageFeatureWeatherBinding private constructor(
    override val root: android.widget.LinearLayout,
    val smartspaceViewTemplateRoot: android.widget.LinearLayout,
    val smartspacePageTemplateBasicClock: IncludeSmartspacePageClockBinding,
    val smartspacePageTemplateBasicSubtitle: IncludeSmartspacePageSubtitleAndActionBinding
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): SmartspacePageFeatureWeatherBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): SmartspacePageFeatureWeatherBinding {
            val root = inflater.inflate(R.layout.smartspace_page_feature_weather, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): SmartspacePageFeatureWeatherBinding {
            val smartspaceViewTemplateRoot = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_view_template_root) ?: throw NullPointerException("Missing required view with ID: smartspace_view_template_root")
            val smartspacePageTemplateBasicClockView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_clock) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_clock")
            val smartspacePageTemplateBasicClock = IncludeSmartspacePageClockBinding.bind(smartspacePageTemplateBasicClockView)
            val smartspacePageTemplateBasicSubtitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_subtitle) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_subtitle")
            val smartspacePageTemplateBasicSubtitle = IncludeSmartspacePageSubtitleAndActionBinding.bind(smartspacePageTemplateBasicSubtitleView)
            return SmartspacePageFeatureWeatherBinding(
                rootView as android.widget.LinearLayout,
                smartspaceViewTemplateRoot,
                smartspacePageTemplateBasicClock,
                smartspacePageTemplateBasicSubtitle
            )
        }
    }
}
