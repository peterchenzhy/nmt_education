package com.nmt.education.listener;


import com.nmt.education.listener.event.TestApplicationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestApplicationListener {

    @EventListener
    public void event(TestApplicationEvent event){
      log.warn( "listener:" +  event.getMessage());
    }
}
