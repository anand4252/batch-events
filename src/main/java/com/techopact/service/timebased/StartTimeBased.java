package com.techopact.service.timebased;

import com.techopact.model.Event;
import lombok.SneakyThrows;
import lombok.val;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Current traffic is 800K per day, which is approx 9 to 10 events/sec
 *
 * Through created is 20 events/ per seconds
 * Edge cases:
 *  1) No events are triggered: Works fine
 *  2) exceptions happens in the Scheduler thread
 */
public class StartTimeBased {

    public static void main(String[] args) {
        System.out.println("Main methods starts.");
        val timeBasedBatchManager = new TimeBasedBatchManager();
        fireEvents(timeBasedBatchManager); //comment to test edge case: NO EVENTS FIRED
        System.out.println("Main methods ends.");
    }

    @SneakyThrows
    private static void fireEvents(TimeBasedBatchManager timeBasedBatchManager){
        fireEventsAsync(timeBasedBatchManager);
        fireEventsFromMainThread(timeBasedBatchManager);
    }

    /**
     * Fires 10 events/per seconds asynchronously
     * @param timeBasedBatchManager
     */
    private static void fireEventsAsync(TimeBasedBatchManager timeBasedBatchManager) {
        ScheduledExecutorService fireEvents = Executors.newSingleThreadScheduledExecutor();
        fireEvents.scheduleAtFixedRate(()-> {
            final Event event = new Event("sameId", "sameName");
            System.out.println("Event  from  Scheduler: " + event);
            timeBasedBatchManager.addEvent(event);
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * Fires 10 events/per seconds synchronously using the main thread
     * @param timeBasedBatchManager
     * @throws InterruptedException
     */
    private static void fireEventsFromMainThread(TimeBasedBatchManager timeBasedBatchManager) throws InterruptedException {
        System.out.println("Firing events from the same thread***** ");
        for (int i = 1; i <= 10000; i++) {
            Thread.sleep(100);
            final Event event = new Event("main_thread_" + i, "eventName_" + i);
            System.out.println("Event from main thread: " + event);
            timeBasedBatchManager.addEvent(event);
        }
    }
}
