package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R
import com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView

class SmartspacePageTemplateCarouselBinding private constructor(
    override val root: android.widget.LinearLayout,
    val smartspacePageTemplateRoot: android.widget.LinearLayout,
    val smartspacePageCarousel: android.widget.LinearLayout,
    val smartspacePageCarouselColumn1: android.widget.LinearLayout,
    val smartspacePageCarouselColumn1Header: android.widget.TextView,
    val smartspacePageCarouselColumn1Icon: com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView,
    val smartspacePageCarouselColumn1Footer: android.widget.TextView,
    val smartspacePageCarouselColumn2: android.widget.LinearLayout,
    val smartspacePageCarouselColumn2Header: android.widget.TextView,
    val smartspacePageCarouselColumn2Icon: com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView,
    val smartspacePageCarouselColumn2Footer: android.widget.TextView,
    val smartspacePageCarouselColumn3: android.widget.LinearLayout,
    val smartspacePageCarouselColumn3Header: android.widget.TextView,
    val smartspacePageCarouselColumn3Icon: com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView,
    val smartspacePageCarouselColumn3Footer: android.widget.TextView,
    val smartspacePageCarouselColumn4: android.widget.LinearLayout,
    val smartspacePageCarouselColumn4Header: android.widget.TextView,
    val smartspacePageCarouselColumn4Icon: com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView,
    val smartspacePageCarouselColumn4Footer: android.widget.TextView,
    val smartspacePageTemplateBasicTitle: IncludeSmartspacePageTitleBinding,
    val smartspacePageTemplateBasicSubtitle: IncludeSmartspacePageSubtitleAndActionBinding,
    val smartspacePageTemplateBasicSupplemental: IncludeSmartspacePageSupplementalBinding
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): SmartspacePageTemplateCarouselBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): SmartspacePageTemplateCarouselBinding {
            val root = inflater.inflate(R.layout.smartspace_page_template_carousel, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): SmartspacePageTemplateCarouselBinding {
            val smartspacePageTemplateRoot = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_template_root) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_root")
            val smartspacePageCarousel = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_carousel) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel")
            val smartspacePageCarouselColumn1 = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_carousel_column_1) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_1")
            val smartspacePageCarouselColumn1Header = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_carousel_column_1_header) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_1_header")
            val smartspacePageCarouselColumn1Icon = rootView.findViewById<com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView>(R.id.smartspace_page_carousel_column_1_icon) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_1_icon")
            val smartspacePageCarouselColumn1Footer = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_carousel_column_1_footer) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_1_footer")
            val smartspacePageCarouselColumn2 = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_carousel_column_2) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_2")
            val smartspacePageCarouselColumn2Header = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_carousel_column_2_header) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_2_header")
            val smartspacePageCarouselColumn2Icon = rootView.findViewById<com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView>(R.id.smartspace_page_carousel_column_2_icon) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_2_icon")
            val smartspacePageCarouselColumn2Footer = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_carousel_column_2_footer) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_2_footer")
            val smartspacePageCarouselColumn3 = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_carousel_column_3) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_3")
            val smartspacePageCarouselColumn3Header = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_carousel_column_3_header) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_3_header")
            val smartspacePageCarouselColumn3Icon = rootView.findViewById<com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView>(R.id.smartspace_page_carousel_column_3_icon) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_3_icon")
            val smartspacePageCarouselColumn3Footer = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_carousel_column_3_footer) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_3_footer")
            val smartspacePageCarouselColumn4 = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_carousel_column_4) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_4")
            val smartspacePageCarouselColumn4Header = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_carousel_column_4_header) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_4_header")
            val smartspacePageCarouselColumn4Icon = rootView.findViewById<com.kieronquinn.app.smartspacer.sdk.client.views.DoubleShadowImageView>(R.id.smartspace_page_carousel_column_4_icon) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_4_icon")
            val smartspacePageCarouselColumn4Footer = rootView.findViewById<android.widget.TextView>(R.id.smartspace_page_carousel_column_4_footer) ?: throw NullPointerException("Missing required view with ID: smartspace_page_carousel_column_4_footer")
            val smartspacePageTemplateBasicTitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_title) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_title")
            val smartspacePageTemplateBasicTitle = IncludeSmartspacePageTitleBinding.bind(smartspacePageTemplateBasicTitleView)
            val smartspacePageTemplateBasicSubtitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_subtitle) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_subtitle")
            val smartspacePageTemplateBasicSubtitle = IncludeSmartspacePageSubtitleAndActionBinding.bind(smartspacePageTemplateBasicSubtitleView)
            val smartspacePageTemplateBasicSupplementalView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_supplemental) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_supplemental")
            val smartspacePageTemplateBasicSupplemental = IncludeSmartspacePageSupplementalBinding.bind(smartspacePageTemplateBasicSupplementalView)
            return SmartspacePageTemplateCarouselBinding(
                rootView as android.widget.LinearLayout,
                smartspacePageTemplateRoot,
                smartspacePageCarousel,
                smartspacePageCarouselColumn1,
                smartspacePageCarouselColumn1Header,
                smartspacePageCarouselColumn1Icon,
                smartspacePageCarouselColumn1Footer,
                smartspacePageCarouselColumn2,
                smartspacePageCarouselColumn2Header,
                smartspacePageCarouselColumn2Icon,
                smartspacePageCarouselColumn2Footer,
                smartspacePageCarouselColumn3,
                smartspacePageCarouselColumn3Header,
                smartspacePageCarouselColumn3Icon,
                smartspacePageCarouselColumn3Footer,
                smartspacePageCarouselColumn4,
                smartspacePageCarouselColumn4Header,
                smartspacePageCarouselColumn4Icon,
                smartspacePageCarouselColumn4Footer,
                smartspacePageTemplateBasicTitle,
                smartspacePageTemplateBasicSubtitle,
                smartspacePageTemplateBasicSupplemental
            )
        }
    }
}
