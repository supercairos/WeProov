package com.weproov.app.models.providers;

import android.os.Handler;
import android.os.Looper;
import com.squareup.otto.Bus;

/**
 * Maintains a singleton instance for obtaining the bus. Ideally this would be replaced with a more efficient means
 * such as through injection directly into interested classes.
 */
public final class BusProvider {
    private static final MainThreadBus BUS = new MainThreadBus();

    public static MainThreadBus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }

    public static class MainThreadBus extends Bus {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void post(final Object event) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event);
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MainThreadBus.super.post(event);
                    }
                });
            }
        }
    }
}
