package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R
import com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView

class SmartspacePageTemplateCardBinding private constructor(
    override val root: android.widget.LinearLayout,
    val smartspacePageTemplateRoot: android.widget.LinearLayout,
    val smartspacePageCard: android.widget.LinearLayout,
    val smartspacePageCardIcon: com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView,
    val smartspacePageCardText: android.widget.TextView,
    val smartspacePageTemplateBasicTitle: IncludeSmartspacePageTitleBinding,
    val smartspacePageTemplateBasicSubtitle: IncludeSmartspacePageSubtitleBinding,
    val smartspacePageTemplateBasicSupplemental: IncludeSmartspacePageSupplementalBinding
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): SmartspacePageTemplateCardBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): SmartspacePageTemplateCardBinding {
            val root = inflater.inflate(R.layout.smartspace_page_template_card, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): SmartspacePageTemplateCardBinding {
            val smartspacePageTemplateRoot = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_template_root) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_root")
            val smartspacePageCard = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_card) ?: throw NullPointerException("Missing required view with ID: smartspace_page_card")
            val smartspacePageCardIcon = rootView.findViewById<com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView>(R.id.smartspace_page_card_icon) ?: throw NullPointerException("Missing required view with ID: smartspace_page_card_icon")
            val smartspacePageCardText = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_card_text) ?: throw NullPointerException("Missing required view with ID: smartspace_page_card_text")
            val smartspacePageTemplateBasicTitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_title) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_title")
            val smartspacePageTemplateBasicTitle = IncludeSmartspacePageTitleBinding.bind(smartspacePageTemplateBasicTitleView)
            val smartspacePageTemplateBasicSubtitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_subtitle) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_subtitle")
            val smartspacePageTemplateBasicSubtitle = IncludeSmartspacePageSubtitleBinding.bind(smartspacePageTemplateBasicSubtitleView)
            val smartspacePageTemplateBasicSupplementalView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_supplemental) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_supplemental")
            val smartspacePageTemplateBasicSupplemental = IncludeSmartspacePageSupplementalBinding.bind(smartspacePageTemplateBasicSupplementalView)
            return SmartspacePageTemplateCardBinding(
                rootView as android.widget.LinearLayout,
                smartspacePageTemplateRoot,
                smartspacePageCard,
                smartspacePageCardIcon,
                smartspacePageCardText,
                smartspacePageTemplateBasicTitle,
                smartspacePageTemplateBasicSubtitle,
                smartspacePageTemplateBasicSupplemental
            )
        }
    }
}
