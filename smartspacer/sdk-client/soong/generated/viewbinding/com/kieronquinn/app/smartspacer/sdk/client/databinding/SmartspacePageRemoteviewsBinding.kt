package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R

class SmartspacePageRemoteviewsBinding private constructor(
    override val root: android.widget.FrameLayout,
    val smartspaceViewRemoteviews: android.widget.FrameLayout
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): SmartspacePageRemoteviewsBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): SmartspacePageRemoteviewsBinding {
            val root = inflater.inflate(R.layout.smartspace_page_remoteviews, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): SmartspacePageRemoteviewsBinding {
            val smartspaceViewRemoteviews = rootView.findViewById<android.widget.FrameLayout>(R.id.smartspace_view_remoteviews) ?: throw NullPointerException("Missing required view with ID: smartspace_view_remoteviews")
            return SmartspacePageRemoteviewsBinding(
                rootView as android.widget.FrameLayout,
                smartspaceViewRemoteviews
            )
        }
    }
}
