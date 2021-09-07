package com.techopact.service.countandtimebased;

import com.techopact.model.Event;
import lombok.SneakyThrows;
import lombok.val;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StartCountAndTimeBased {

    public static void main(String[] args) {
        System.out.println("Main methods starts.");
        val countAndTimeBasedBatchManager = new CountAndTimeBasedBatchManager();
        fireEvents(countAndTimeBasedBatchManager); //comment to test edge case: NO EVENTS FIRED
        System.out.println("Main methods ends.");
    }


    private static void fireEvents(CountAndTimeBasedBatchManager countAndTimeBasedBatchManager){
        fireEventsAsync(countAndTimeBasedBatchManager);
        fireEventsFromMainThread(countAndTimeBasedBatchManager);
    }

    /**
     * Fire 10 events/per seconds asynchronously
     * @param countAndTimeBasedBatchManager Count and time based manager
     */
    private static void fireEventsAsync(CountAndTimeBasedBatchManager countAndTimeBasedBatchManager) {
        ScheduledExecutorService fireEvents = Executors.newSingleThreadScheduledExecutor();
        fireEvents.scheduleAtFixedRate(()-> {
            final Event event = new Event("sameId", "sameName");
            System.out.println("Event  from  Scheduler: " + event);
            countAndTimeBasedBatchManager.addEvent(event);
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * Fire 10 events/per seconds synchronously using the main thread
     * @param countAndTimeBasedBatchManager Count and time based manager.
     */
    @SneakyThrows
    private static void fireEventsFromMainThread(CountAndTimeBasedBatchManager countAndTimeBasedBatchManager) {
        System.out.println("Firing events from the same thread ***** ");
        for (int i = 1; i <= 10000; i++) {
            Thread.sleep(100);
            final Event event = new Event("main_thread_" + i, "eventName_" + i);
            System.out.println("Event from main thread: " + event);
            countAndTimeBasedBatchManager.addEvent(event);
        }
    }
}
