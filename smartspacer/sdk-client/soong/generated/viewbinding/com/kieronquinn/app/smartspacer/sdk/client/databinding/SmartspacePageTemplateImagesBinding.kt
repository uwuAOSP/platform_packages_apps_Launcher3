package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R

class SmartspacePageTemplateImagesBinding private constructor(
    override val root: android.widget.LinearLayout,
    val smartspacePageTemplateRoot: android.widget.LinearLayout,
    val smartspacePageImages: androidx.constraintlayout.widget.ConstraintLayout,
    val smartspacePageImagesImage: android.widget.ImageView,
    val smartspacePageTemplateBasicTitle: IncludeSmartspacePageTitleBinding,
    val smartspacePageTemplateBasicSubtitle: IncludeSmartspacePageSubtitleBinding,
    val smartspacePageTemplateBasicSupplemental: IncludeSmartspacePageSupplementalBinding
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): SmartspacePageTemplateImagesBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): SmartspacePageTemplateImagesBinding {
            val root = inflater.inflate(R.layout.smartspace_page_template_images, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): SmartspacePageTemplateImagesBinding {
            val smartspacePageTemplateRoot = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_template_root) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_root")
            val smartspacePageImages = rootView.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.smartspace_page_images) ?: throw NullPointerException("Missing required view with ID: smartspace_page_images")
            val smartspacePageImagesImage = rootView.findViewById<android.widget.ImageView>(R.id.smartspace_page_images_image) ?: throw NullPointerException("Missing required view with ID: smartspace_page_images_image")
            val smartspacePageTemplateBasicTitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_title) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_title")
            val smartspacePageTemplateBasicTitle = IncludeSmartspacePageTitleBinding.bind(smartspacePageTemplateBasicTitleView)
            val smartspacePageTemplateBasicSubtitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_subtitle) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_subtitle")
            val smartspacePageTemplateBasicSubtitle = IncludeSmartspacePageSubtitleBinding.bind(smartspacePageTemplateBasicSubtitleView)
            val smartspacePageTemplateBasicSupplementalView = rootView.findViewById<android.view.View>(R.id.smartspace_page_template_basic_supplemental) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_basic_supplemental")
            val smartspacePageTemplateBasicSupplemental = IncludeSmartspacePageSupplementalBinding.bind(smartspacePageTemplateBasicSupplementalView)
            return SmartspacePageTemplateImagesBinding(
                rootView as android.widget.LinearLayout,
                smartspacePageTemplateRoot,
                smartspacePageImages,
                smartspacePageImagesImage,
                smartspacePageTemplateBasicTitle,
                smartspacePageTemplateBasicSubtitle,
                smartspacePageTemplateBasicSupplemental
            )
        }
    }
}
