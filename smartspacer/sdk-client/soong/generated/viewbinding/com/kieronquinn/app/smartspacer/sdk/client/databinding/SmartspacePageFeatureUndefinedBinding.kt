package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R

class SmartspacePageFeatureUndefinedBinding private constructor(
    override val root: android.widget.LinearLayout,
    val smartspacePageFeatureRoot: android.widget.LinearLayout,
    val smartspacePageFeatureUndefinedTitle: IncludeSmartspacePageTitleBinding,
    val smartspacePageFeatureUndefinedSubtitle: IncludeSmartspacePageSubtitleAndActionBinding
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): SmartspacePageFeatureUndefinedBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): SmartspacePageFeatureUndefinedBinding {
            val root = inflater.inflate(R.layout.smartspace_page_feature_undefined, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): SmartspacePageFeatureUndefinedBinding {
            val smartspacePageFeatureRoot = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_feature_root) ?: throw NullPointerException("Missing required view with ID: smartspace_page_feature_root")
            val smartspacePageFeatureUndefinedTitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_feature_undefined_title) ?: throw NullPointerException("Missing required view with ID: smartspace_page_feature_undefined_title")
            val smartspacePageFeatureUndefinedTitle = IncludeSmartspacePageTitleBinding.bind(smartspacePageFeatureUndefinedTitleView)
            val smartspacePageFeatureUndefinedSubtitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_feature_undefined_subtitle) ?: throw NullPointerException("Missing required view with ID: smartspace_page_feature_undefined_subtitle")
            val smartspacePageFeatureUndefinedSubtitle = IncludeSmartspacePageSubtitleAndActionBinding.bind(smartspacePageFeatureUndefinedSubtitleView)
            return SmartspacePageFeatureUndefinedBinding(
                rootView as android.widget.LinearLayout,
                smartspacePageFeatureRoot,
                smartspacePageFeatureUndefinedTitle,
                smartspacePageFeatureUndefinedSubtitle
            )
        }
    }
}
