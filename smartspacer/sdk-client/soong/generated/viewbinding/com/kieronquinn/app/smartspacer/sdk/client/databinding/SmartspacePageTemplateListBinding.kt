package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R
import com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView

class SmartspacePageTemplateListBinding private constructor(
    override val root: android.widget.LinearLayout,
    val smartspaceViewTemplateRoot: android.widget.LinearLayout,
    val smartspaceViewList: android.widget.LinearLayout,
    val smartspaceViewListItem1: android.widget.TextView,
    val smartspaceViewListItem2: android.widget.TextView,
    val smartspaceViewListItem3: android.widget.TextView,
    val smartspaceViewListIcon: com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView,
    val smartspacePageTemplateBasicTitle: IncludeSmartspacePageTitleBinding,
    val smartspacePageTemplateBasicSubtitle: IncludeSmartspacePageSubtitleBinding,
    val smartspacePageTemplateBasicSupplemental: IncludeSmartspacePageSupplementalBinding
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): SmartspacePageTemplateListBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): SmartspacePageTemplateListBinding {
            val root = inflater.inflate(R.layout.smartspace_page_template_list, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): SmartspacePageTemplateListBinding {
            val smartspaceViewTemplateRoot = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_view_template_root) ?: throw NullPointerException("Missing required view with ID: smartspace_view_template_root")
            val smartspaceViewList = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_view_list) ?: throw NullPointerException("Missing required view with ID: smartspace_view_list")
            val smartspaceViewListItem1 = rootView.findViewById<android.widget.TextView>(R.id.smartspace_view_list_item_1) ?: throw NullPointerException("Missing required view with ID: smartspace_view_list_item_1")
            val smartspaceViewListItem2 = rootView.findViewById<android.widget.TextView>(R.id.smartspace_view_list_item_2) ?: throw NullPointerException("Missing required view with ID: smartspace_view_list_item_2")
            val smartspaceViewListItem3 = rootView.findViewById<android.widget.TextView>(R.id.smartspace_view_list_item_3) ?: throw NullPointerException("Missing required view with ID: smartspace_view_list_item_3")
            val smartspaceViewListIcon = rootView.findViewById<com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView>(R.id.smartspace_view_list_icon) ?: throw NullPointerException("Missing required view with ID: smartspace_view_list_icon")
            val smartspacePageTemplateBasicTitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_title) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_title")
            val smartspacePageTemplateBasicTitle = IncludeSmartspacePageTitleBinding.bind(smartspacePageTemplateBasicTitleView)
            val smartspacePageTemplateBasicSubtitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_subtitle) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_subtitle")
            val smartspacePageTemplateBasicSubtitle = IncludeSmartspacePageSubtitleBinding.bind(smartspacePageTemplateBasicSubtitleView)
            val smartspacePageTemplateBasicSupplementalView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_supplemental) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_supplemental")
            val smartspacePageTemplateBasicSupplemental = IncludeSmartspacePageSupplementalBinding.bind(smartspacePageTemplateBasicSupplementalView)
            return SmartspacePageTemplateListBinding(
                rootView as android.widget.LinearLayout,
                smartspaceViewTemplateRoot,
                smartspaceViewList,
                smartspaceViewListItem1,
                smartspaceViewListItem2,
                smartspaceViewListItem3,
                smartspaceViewListIcon,
                smartspacePageTemplateBasicTitle,
                smartspacePageTemplateBasicSubtitle,
                smartspacePageTemplateBasicSupplemental
            )
        }
    }
}
