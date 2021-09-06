package com.techopact;

import com.techopact.Utils;
import com.techopact.model.Event;

import java.util.List;

public class FbPublisher {

    public void sendBatchEvent(List<Event> events){
//        Utils.throwExceptionRandomly();
        System.out.println("FbPublisher Sending events: " + events.size());
    }
}
