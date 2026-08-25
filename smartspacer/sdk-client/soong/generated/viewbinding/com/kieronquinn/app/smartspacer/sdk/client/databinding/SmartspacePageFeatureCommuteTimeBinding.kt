package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R

class SmartspacePageFeatureCommuteTimeBinding private constructor(
    override val root: android.widget.LinearLayout,
    val smartspacePageTemplateRoot: android.widget.LinearLayout,
    val smartspacePageCommuteTime: android.widget.FrameLayout,
    val smartspacePageCommuteTimeImage: android.widget.ImageView,
    val smartspacePageFeatureBasicTitle: IncludeSmartspacePageTitleBinding,
    val smartspacePageFeatureBasicSubtitle: IncludeSmartspacePageSubtitleBinding
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): SmartspacePageFeatureCommuteTimeBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): SmartspacePageFeatureCommuteTimeBinding {
            val root = inflater.inflate(R.layout.smartspace_page_feature_commute_time, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): SmartspacePageFeatureCommuteTimeBinding {
            val smartspacePageTemplateRoot = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_template_root) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_root")
            val smartspacePageCommuteTime = rootView.findViewById<android.widget.FrameLayout>(R.id.smartspace_page_commute_time) ?: throw NullPointerException("Missing required view with ID: smartspace_page_commute_time")
            val smartspacePageCommuteTimeImage = rootView.findViewById<android.widget.ImageView>(R.id.smartspace_page_commute_time_image) ?: throw NullPointerException("Missing required view with ID: smartspace_page_commute_time_image")
            val smartspacePageFeatureBasicTitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_feature_basic_title) ?: throw NullPointerException("Missing required view with ID: smartspace_page_feature_basic_title")
            val smartspacePageFeatureBasicTitle = IncludeSmartspacePageTitleBinding.bind(smartspacePageFeatureBasicTitleView)
            val smartspacePageFeatureBasicSubtitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_feature_basic_subtitle) ?: throw NullPointerException("Missing required view with ID: smartspace_page_feature_basic_subtitle")
            val smartspacePageFeatureBasicSubtitle = IncludeSmartspacePageSubtitleBinding.bind(smartspacePageFeatureBasicSubtitleView)
            return SmartspacePageFeatureCommuteTimeBinding(
                rootView as android.widget.LinearLayout,
                smartspacePageTemplateRoot,
                smartspacePageCommuteTime,
                smartspacePageCommuteTimeImage,
                smartspacePageFeatureBasicTitle,
                smartspacePageFeatureBasicSubtitle
            )
        }
    }
}
