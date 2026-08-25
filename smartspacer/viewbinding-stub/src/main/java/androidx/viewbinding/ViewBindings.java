package androidx.viewbinding;

import android.view.View;

public final class ViewBindings {

    private ViewBindings() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findChildViewById(View rootView, int id) {
        return (T) rootView.findViewById(id);
    }
}
