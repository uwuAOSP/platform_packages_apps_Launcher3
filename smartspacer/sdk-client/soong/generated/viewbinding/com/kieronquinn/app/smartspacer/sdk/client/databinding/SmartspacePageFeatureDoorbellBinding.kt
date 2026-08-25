package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R
import com.kieronquinn.app.smartspacer.sdk.client.views.SafeViewFlipper

class SmartspacePageFeatureDoorbellBinding private constructor(
    override val root: android.widget.LinearLayout,
    val smartspacePageTemplateRoot: android.widget.LinearLayout,
    val smartspacePageDoorbell: com.kieronquinn.app.smartspacer.sdk.client.views.SafeViewFlipper,
    val smartspacePageDoorbellLoadingIndeterminateContainer: androidx.constraintlayout.widget.ConstraintLayout,
    val smartspacePageDoorbellLoadingIndeterminate: android.widget.ProgressBar,
    val smartspacePageDoorbellLoadingContainer: androidx.constraintlayout.widget.ConstraintLayout,
    val smartspacePageDoorbellLoadingImage: android.widget.ImageView,
    val smartspacePageDoorbellLoadingProgress: android.widget.ProgressBar,
    val smartspacePageDoorbellVideocamContainer: androidx.constraintlayout.widget.ConstraintLayout,
    val smartspacePageDoorbellVideocam: android.widget.ImageView,
    val smartspacePageDoorbellVideocamOffContainer: androidx.constraintlayout.widget.ConstraintLayout,
    val smartspacePageDoorbellVideocamOff: android.widget.ImageView,
    val smartspacePageDoorbellImageBitmapContainer: androidx.constraintlayout.widget.ConstraintLayout,
    val smartspacePageDoorbellImageBitmap: android.widget.ImageView,
    val smartspacePageDoorbellImageUriContainer: androidx.constraintlayout.widget.ConstraintLayout,
    val smartspacePageDoorbellImageUri: android.widget.ImageView,
    val smartspacePageFeatureBasicTitle: IncludeSmartspacePageTitleBinding,
    val smartspacePageFeatureBasicSubtitle: IncludeSmartspacePageSubtitleBinding
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): SmartspacePageFeatureDoorbellBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): SmartspacePageFeatureDoorbellBinding {
            val root = inflater.inflate(R.layout.smartspace_page_feature_doorbell, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): SmartspacePageFeatureDoorbellBinding {
            val smartspacePageTemplateRoot = rootView.findViewById<android.widget.LinearLayout>(R.id.smartspace_page_template_root) ?: throw NullPointerException("Missing required view with ID: smartspace_page_template_root")
            val smartspacePageDoorbell = rootView.findViewById<com.kieronquinn.app.smartspacer.sdk.client.views.SafeViewFlipper>(R.id.smartspace_page_doorbell) ?: throw NullPointerException("Missing required view with ID: smartspace_page_doorbell")
            val smartspacePageDoorbellLoadingIndeterminateContainer = rootView.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.smartspace_page_doorbell_loading_indeterminate_container) ?: throw NullPointerException("Missing required view with ID: smartspace_page_doorbell_loading_indeterminate_container")
            val smartspacePageDoorbellLoadingIndeterminate = rootView.findViewById<android.widget.ProgressBar>(R.id.smartspace_page_doorbell_loading_indeterminate) ?: throw NullPointerException("Missing required view with ID: smartspace_page_doorbell_loading_indeterminate")
            val smartspacePageDoorbellLoadingContainer = rootView.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.smartspace_page_doorbell_loading_container) ?: throw NullPointerException("Missing required view with ID: smartspace_page_doorbell_loading_container")
            val smartspacePageDoorbellLoadingImage = rootView.findViewById<android.widget.ImageView>(R.id.smartspace_page_doorbell_loading_image) ?: throw NullPointerException("Missing required view with ID: smartspace_page_doorbell_loading_image")
            val smartspacePageDoorbellLoadingProgress = rootView.findViewById<android.widget.ProgressBar>(R.id.smartspace_page_doorbell_loading_progress) ?: throw NullPointerException("Missing required view with ID: smartspace_page_doorbell_loading_progress")
            val smartspacePageDoorbellVideocamContainer = rootView.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.smartspace_page_doorbell_videocam_container) ?: throw NullPointerException("Missing required view with ID: smartspace_page_doorbell_videocam_container")
            val smartspacePageDoorbellVideocam = rootView.findViewById<android.widget.ImageView>(R.id.smartspace_page_doorbell_videocam) ?: throw NullPointerException("Missing required view with ID: smartspace_page_doorbell_videocam")
            val smartspacePageDoorbellVideocamOffContainer = rootView.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.smartspace_page_doorbell_videocam_off_container) ?: throw NullPointerException("Missing required view with ID: smartspace_page_doorbell_videocam_off_container")
            val smartspacePageDoorbellVideocamOff = rootView.findViewById<android.widget.ImageView>(R.id.smartspace_page_doorbell_videocam_off) ?: throw NullPointerException("Missing required view with ID: smartspace_page_doorbell_videocam_off")
            val smartspacePageDoorbellImageBitmapContainer = rootView.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.smartspace_page_doorbell_image_bitmap_container) ?: throw NullPointerException("Missing required view with ID: smartspace_page_doorbell_image_bitmap_container")
            val smartspacePageDoorbellImageBitmap = rootView.findViewById<android.widget.ImageView>(R.id.smartspace_page_doorbell_image_bitmap) ?: throw NullPointerException("Missing required view with ID: smartspace_page_doorbell_image_bitmap")
            val smartspacePageDoorbellImageUriContainer = rootView.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.smartspace_page_doorbell_image_uri_container) ?: throw NullPointerException("Missing required view with ID: smartspace_page_doorbell_image_uri_container")
            val smartspacePageDoorbellImageUri = rootView.findViewById<android.widget.ImageView>(R.id.smartspace_page_doorbell_image_uri) ?: throw NullPointerException("Missing required view with ID: smartspace_page_doorbell_image_uri")
            val smartspacePageFeatureBasicTitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_feature_basic_title) ?: throw NullPointerException("Missing required view with ID: smartspace_page_feature_basic_title")
            val smartspacePageFeatureBasicTitle = IncludeSmartspacePageTitleBinding.bind(smartspacePageFeatureBasicTitleView)
            val smartspacePageFeatureBasicSubtitleView = rootView.findViewById<android.view.View>(R.id.smartspace_page_feature_basic_subtitle) ?: throw NullPointerException("Missing required view with ID: smartspace_page_feature_basic_subtitle")
            val smartspacePageFeatureBasicSubtitle = IncludeSmartspacePageSubtitleBinding.bind(smartspacePageFeatureBasicSubtitleView)
            return SmartspacePageFeatureDoorbellBinding(
                rootView as android.widget.LinearLayout,
                smartspacePageTemplateRoot,
                smartspacePageDoorbell,
                smartspacePageDoorbellLoadingIndeterminateContainer,
                smartspacePageDoorbellLoadingIndeterminate,
                smartspacePageDoorbellLoadingContainer,
                smartspacePageDoorbellLoadingImage,
                smartspacePageDoorbellLoadingProgress,
                smartspacePageDoorbellVideocamContainer,
                smartspacePageDoorbellVideocam,
                smartspacePageDoorbellVideocamOffContainer,
                smartspacePageDoorbellVideocamOff,
                smartspacePageDoorbellImageBitmapContainer,
                smartspacePageDoorbellImageBitmap,
                smartspacePageDoorbellImageUriContainer,
                smartspacePageDoorbellImageUri,
                smartspacePageFeatureBasicTitle,
                smartspacePageFeatureBasicSubtitle
            )
        }
    }
}
