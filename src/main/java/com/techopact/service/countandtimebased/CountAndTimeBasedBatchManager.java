package com.techopact.service.countandtimebased;

import com.techopact.FbPublisher;
import com.techopact.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CountAndTimeBasedBatchManager {
    public static final int BATCH_SIZE_THRESHOLD = 100;
    public static final int BATCH_TIME_THRESHOLD_IN_SECONDS = 20;
    public final List<Event> batchedEvents;
    private final FbPublisher fbPublisher;
    private CountDownLatch countDownLatch = new CountDownLatch(BATCH_SIZE_THRESHOLD);

    public CountAndTimeBasedBatchManager() {
        batchedEvents = new ArrayList<>(BATCH_SIZE_THRESHOLD);
        fbPublisher = new FbPublisher();
        /*
            - create a Thread which runs at a scheduled time(Ex: 20 Seconds)
            - Runs without a delay
            - Runs every ms
            - Thread is blocked by the countDownLatch until one of the below condition is true
                # until specified time elapses
                # until the batch size is reached.
         */
        ScheduledExecutorService fbEventSenderService = Executors.newSingleThreadScheduledExecutor();
        fbEventSenderService.scheduleAtFixedRate(() -> {
            try {
                countDownLatch.await(BATCH_TIME_THRESHOLD_IN_SECONDS, TimeUnit.SECONDS);
                performCountAndTimeBasedTask();
                countDownLatch = new CountDownLatch(BATCH_SIZE_THRESHOLD); //reset countdown
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.MILLISECONDS);
    }

    private void performCountAndTimeBasedTask() {
        try {
            System.out.println("Action performed... ");
            List<Event> eventsToBeSent = null;
            synchronized (batchedEvents) { // Only the lock for as less time as possible
                if (!batchedEvents.isEmpty()) {
                    eventsToBeSent = new ArrayList<>(batchedEvents);
                    System.out.println("eventsToBeSent list:: " + eventsToBeSent);
//                    Utils.throwExceptionRandomly(); //uncomment to test Edge case: Exception thrown
                    batchedEvents.clear();
                }
            }
            if (eventsToBeSent != null && !eventsToBeSent.isEmpty()) {
                fbPublisher.sendBatchEvent(eventsToBeSent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs the following
     * <ul>
     *     <li>adds the event to the shared collection </li>
     *     <li>decrements the count in the countdown latch</li>
     * </ul>
     *
     * @param event event to be added
     */
    public void addEvent(Event event) {
        synchronized (this.batchedEvents){
            this.batchedEvents.add(event);
        }
        countDownLatch.countDown();
    }
}
