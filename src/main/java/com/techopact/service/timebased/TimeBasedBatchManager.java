package com.techopact.service.timebased;

import com.techopact.Utils;
import com.techopact.model.Event;
import com.techopact.FbPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeBasedBatchManager {
    public static final int BATCH_SIZE_THRESHOLD = 100; // used only to avoid grow() operation of arraylist. reduce the CPU
    public static final int BATCH_TIME_THRESHOLD_IN_SECONDS = 5; //Frequency at which we want to send the events
    private final List<Event> batchedEvents; //Holds the events from all threads.
    private final FbPublisher fbPublisher;

    public TimeBasedBatchManager() {
        batchedEvents = new ArrayList<>(BATCH_SIZE_THRESHOLD);
        fbPublisher = new FbPublisher();
        ScheduledExecutorService fbEventSenderService = Executors.newSingleThreadScheduledExecutor(); // Creating a thread scheduler: start with the delay of 5 seconds, after that runs every 5 seconds
        fbEventSenderService.scheduleAtFixedRate(new TimeBasedTask(), 0, BATCH_TIME_THRESHOLD_IN_SECONDS, TimeUnit.SECONDS);
    }

    public void addEvent(Event event) {// This method is called by call the threads
        synchronized (batchedEvents) {
            this.batchedEvents.add(event);
        }
    }

    private class TimeBasedTask implements Runnable {
        @Override
        public void run() {
            {
                try {
                    System.out.println("FB scheduler checking.... ");
                    List<Event> eventsToBeSent = null;
                    synchronized (batchedEvents) {
                        if (!batchedEvents.isEmpty()) { //Reason for copying to another array: to hold the lock for as less time as possible, so that the new events can be added.
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
        }
    }


}
