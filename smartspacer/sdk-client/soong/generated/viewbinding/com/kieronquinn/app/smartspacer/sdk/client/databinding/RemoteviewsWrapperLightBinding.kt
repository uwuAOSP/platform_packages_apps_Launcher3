package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R

class RemoteviewsWrapperLightBinding private constructor(
    override val root: android.widget.FrameLayout,
    val remoteviewsWrapper: android.widget.FrameLayout
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): RemoteviewsWrapperLightBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): RemoteviewsWrapperLightBinding {
            val root = inflater.inflate(R.layout.remoteviews_wrapper_light, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): RemoteviewsWrapperLightBinding {
            val remoteviewsWrapper = rootView.findViewById<android.widget.FrameLayout>(R.id.remoteviews_wrapper) ?: throw NullPointerException("Missing required view with ID: remoteviews_wrapper")
            return RemoteviewsWrapperLightBinding(
                rootView as android.widget.FrameLayout,
                remoteviewsWrapper
            )
        }
    }
}
