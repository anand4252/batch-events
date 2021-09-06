package com.techopact.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Event {
    private String eventId;
    private String eventName;
}
