package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R
import com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView

class SmartspacePageTemplateHeadToHeadBinding private constructor(
    override val root: android.widget.LinearLayout,
    val smartspacePageTemplateRoot: android.widget.LinearLayout,
    val smartspacePageHeadToHead: android.widget.RelativeLayout,
    val smartspacePageHeadToHeadTitle: android.widget.TextView,
    val smartspacePageHeadToHead1Icon: com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView,
    val smartspacePageHeadToHead1Text: android.widget.TextView,
    val smartspacePageHeadToHead2Icon: com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView,
    val smartspacePageHeadToHead2Text: android.widget.TextView,
    val smartspacePageTemplateBasicTitle: IncludeSmartspacePageTitleBinding,
    val smartspacePageTemplateBasicSubtitle: IncludeSmartspacePageSubtitleBinding,
    val smartspacePageTemplateBasicSupplemental: IncludeSmartspacePageSupplementalBinding
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): SmartspacePageTemplateHeadToHeadBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): SmartspacePageTemplateHeadToHeadBinding {
            val root = inflater.inflate(R.layout.smartspace_page_template_head_to_head, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): SmartspacePageTemplateHeadToHeadBinding {
            val smartspacePageTemplateRoot = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_template_root) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_root")
            val smartspacePageHeadToHead = rootView.findViewById<android.widget.RelativeLayout>(R.id.smartspace_page_head_to_head) ?: throw NullPointerException("Missing required view with ID: smartspace_page_head_to_head")
            val smartspacePageHeadToHeadTitle = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_head_to_head_title) ?: throw NullPointerException("Missing required view with ID: smartspace_page_head_to_head_title")
            val smartspacePageHeadToHead1Icon = rootView.findViewById<com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView>(R.id.smartspace_page_head_to_head_1_icon) ?: throw NullPointerException("Missing required view with ID: smartspace_page_head_to_head_1_icon")
            val smartspacePageHeadToHead1Text = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_head_to_head_1_text) ?: throw NullPointerException("Missing required view with ID: smartspace_page_head_to_head_1_text")
            val smartspacePageHeadToHead2Icon = rootView.findViewById<com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView>(R.id.smartspace_page_head_to_head_2_icon) ?: throw NullPointerException("Missing required view with ID: smartspace_page_head_to_head_2_icon")
            val smartspacePageHeadToHead2Text = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_head_to_head_2_text) ?: throw NullPointerException("Missing required view with ID: smartspace_page_head_to_head_2_text")
            val smartspacePageTemplateBasicTitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_title) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_title")
            val smartspacePageTemplateBasicTitle = IncludeSmartspacePageTitleBinding.bind(smartspacePageTemplateBasicTitleView)
            val smartspacePageTemplateBasicSubtitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_subtitle) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_subtitle")
            val smartspacePageTemplateBasicSubtitle = IncludeSmartspacePageSubtitleBinding.bind(smartspacePageTemplateBasicSubtitleView)
            val smartspacePageTemplateBasicSupplementalView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_supplemental) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_supplemental")
            val smartspacePageTemplateBasicSupplemental = IncludeSmartspacePageSupplementalBinding.bind(smartspacePageTemplateBasicSupplementalView)
            return SmartspacePageTemplateHeadToHeadBinding(
                rootView as android.widget.LinearLayout,
                smartspacePageTemplateRoot,
                smartspacePageHeadToHead,
                smartspacePageHeadToHeadTitle,
                smartspacePageHeadToHead1Icon,
                smartspacePageHeadToHead1Text,
                smartspacePageHeadToHead2Icon,
                smartspacePageHeadToHead2Text,
                smartspacePageTemplateBasicTitle,
                smartspacePageTemplateBasicSubtitle,
                smartspacePageTemplateBasicSupplemental
            )
        }
    }
}
