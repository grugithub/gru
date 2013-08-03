package com.github.gru;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * User: mdehaan
 * Date: 8/2/13
 */
public class IdleWatcher implements Runnable {

    // Amount of time file system must be idle before processing
    public static final int IDLE_MS = 1000;

    private Object waitObject = new Object();

    private volatile boolean externalWaitRequested = false;
    private volatile boolean remainIdle = false;

    private List<EventData> eventPool = Collections.synchronizedList(new ArrayList<EventData>());

    private FileChangedEvent fileChangedEvent;

    public IdleWatcher(FileChangedEvent fileChangedEvent) {
        this.fileChangedEvent = fileChangedEvent;
    }

    @Override
    public void run() {

        while (true) {

            if (!remainIdle) {
                synchronized (eventPool) {
                    // Analyze the event pool and merge delete+create events for the same file

                    if (eventPool.size() > 0) {
                        List<EventData> cleanEventPool = analyzeAndCleanEventPool();

                        // Clear the event pool
                        eventPool.clear();

                        // Trigger the events
                        for (EventData event : cleanEventPool) {
                            fileChangedEvent.fileChanged(event.getEventType(), event.getPath());
                        }
                    }
                }
            }

            try {

                synchronized (waitObject) {
                    waitObject.wait(IDLE_MS);
                    remainIdle = false;

                    // If we were woken up early, remain idle.
                    if (externalWaitRequested) {
                        externalWaitRequested = false;
                        remainIdle = true;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<EventData> analyzeAndCleanEventPool() {
        List<EventData> cleanEventPool = new ArrayList<EventData>();

        for (int i = 0; i < eventPool.size(); i++) {
            EventData event = eventPool.get(i);

            boolean keepOriginalEvent = true;

            if (event.getEventType() == ENTRY_DELETE) {
                // Look ahead for a CREATE event for the same file.
                for (int a = i; a < eventPool.size(); a++) {
                    EventData futureEvent = eventPool.get(a);

                    // If the future event is a 'create' and the path matches...
                    if (futureEvent.getEventType() == ENTRY_CREATE &&
                            futureEvent.getPath().equals(event.getPath())) {

                        // do not add the delete or the create
                        // Instead, add a modify event
                        EventData cleanEvent = new EventData(ENTRY_MODIFY, event.getPath());
                        cleanEventPool.add(cleanEvent);
                        eventPool.remove(a);

                        // Do not keep the original delete event
                        keepOriginalEvent = false;

                        // We're done looking ahead
                        break;
                    }
                }
            }

            if (keepOriginalEvent) {
                cleanEventPool.add(event);
            }
        }

        return cleanEventPool;
    }

    public void remainIdle() {
        synchronized (waitObject) {
            externalWaitRequested = true;
            waitObject.notifyAll();
        }
    }

    public void addEvent(EventData event) {
        synchronized (eventPool) {
            eventPool.add(event);
        }
    }
}
