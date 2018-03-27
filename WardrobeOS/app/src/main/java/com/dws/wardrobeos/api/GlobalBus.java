package com.dws.wardrobeos.api;

import org.greenrobot.eventbus.EventBus;

public class GlobalBus {
    private static EventBus bus;
    public static EventBus getBus() {
        if (bus == null) {
            bus = EventBus.getDefault();
        }
        return bus;
    }
}
