package com.nmt.education.listener.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestApplicationEvent {

    public TestApplicationEvent(String message) {
        this.message = message;
    }

    private String message;
}
