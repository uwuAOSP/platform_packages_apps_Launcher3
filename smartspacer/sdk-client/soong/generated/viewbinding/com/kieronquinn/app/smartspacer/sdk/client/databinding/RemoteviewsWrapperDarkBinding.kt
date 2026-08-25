package com.kieronquinn.app.smartspacer.sdk.client.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.smartspacer.sdk.client.R

class RemoteviewsWrapperDarkBinding private constructor(
    override val root: android.widget.FrameLayout,
    val remoteviewsWrapper: android.widget.FrameLayout
) : ViewBinding {

    companion object {
        @JvmStatic
        fun inflate(inflater: LayoutInflater): RemoteviewsWrapperDarkBinding =
            inflate(inflater, null, false)

        @JvmStatic
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): RemoteviewsWrapperDarkBinding {
            val root = inflater.inflate(R.layout.remoteviews_wrapper_dark, parent, false)
            if (attachToParent) {
                parent?.addView(root)
            }
            return bind(root)
        }

        @JvmStatic
        fun bind(rootView: View): RemoteviewsWrapperDarkBinding {
            val remoteviewsWrapper = rootView.findViewById<android.widget.FrameLayout>(R.id.remoteviews_wrapper) ?: throw NullPointerException("Missing required view with ID: remoteviews_wrapper")
            return RemoteviewsWrapperDarkBinding(
                rootView as android.widget.FrameLayout,
                remoteviewsWrapper
            )
        }
    }
}
