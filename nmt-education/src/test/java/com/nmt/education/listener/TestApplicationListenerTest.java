package com.nmt.education.listener;

import com.nmt.education.BaseTest;
import com.nmt.education.commmons.utils.SpringContextUtil;
import com.nmt.education.listener.event.TestApplicationEvent;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestApplicationListenerTest extends BaseTest {

    @SneakyThrows
    @Test
    void event() {
        SpringContextUtil.getApplicationContext().publishEvent(new TestApplicationEvent("new test Event1 "));
        Thread.sleep(5*1000);
        SpringContextUtil.getApplicationContext().publishEvent(new TestApplicationEvent("new test Event2 "));
    }
}