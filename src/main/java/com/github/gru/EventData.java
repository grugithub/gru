package com.github.gru;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * User: mdehaan
 * Date: 8/2/13
 */
public class EventData {
    private WatchEvent.Kind<?> eventType;
    private Path path;

    public EventData(WatchEvent.Kind<?> eventType, Path path) {
        this.eventType = eventType;
        this.path = path;
    }

    public WatchEvent.Kind<?> getEventType() {
        return eventType;
    }

    public void setEventType(WatchEvent.Kind<?> eventType) {
        this.eventType = eventType;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
