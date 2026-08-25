package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R

class SmartspacePageTemplateBasicBinding private constructor(
    override val root: android.widget.LinearLayout,
    val smartspaceViewTemplateRoot: android.widget.LinearLayout,
    val smartspacePageTemplateBasicTitle: IncludeSmartspacePageTitleBinding,
    val smartspacePageTemplateBasicSubtitle: IncludeSmartspacePageSubtitleAndActionBinding,
    val smartspacePageTemplateBasicSupplemental: IncludeSmartspacePageSupplementalBinding
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): SmartspacePageTemplateBasicBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): SmartspacePageTemplateBasicBinding {
            val root = inflater.inflate(R.layout.smartspace_page_template_basic, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): SmartspacePageTemplateBasicBinding {
            val smartspaceViewTemplateRoot = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_view_template_root) ?: throw NullPointerException("Missing required view with ID: smartspace_view_template_root")
            val smartspacePageTemplateBasicTitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_title) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_title")
            val smartspacePageTemplateBasicTitle = IncludeSmartspacePageTitleBinding.bind(smartspacePageTemplateBasicTitleView)
            val smartspacePageTemplateBasicSubtitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_subtitle) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_subtitle")
            val smartspacePageTemplateBasicSubtitle = IncludeSmartspacePageSubtitleAndActionBinding.bind(smartspacePageTemplateBasicSubtitleView)
            val smartspacePageTemplateBasicSupplementalView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_supplemental) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_supplemental")
            val smartspacePageTemplateBasicSupplemental = IncludeSmartspacePageSupplementalBinding.bind(smartspacePageTemplateBasicSupplementalView)
            return SmartspacePageTemplateBasicBinding(
                rootView as android.widget.LinearLayout,
                smartspaceViewTemplateRoot,
                smartspacePageTemplateBasicTitle,
                smartspacePageTemplateBasicSubtitle,
                smartspacePageTemplateBasicSupplemental
            )
        }
    }
}
