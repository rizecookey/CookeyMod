package net.rizecookey.cookeymod.event;

import java.util.ArrayList;
import java.util.List;

public interface OverlayReloadListener {
    List<OverlayReloadListener> listeners = new ArrayList<>();
    void onOverlayReload();

    static void register(OverlayReloadListener listener) {
        listeners.add(listener);
    }

    static void callEvent() {
        for (OverlayReloadListener listener : listeners) {
            listener.onOverlayReload();
        }
    }
}
