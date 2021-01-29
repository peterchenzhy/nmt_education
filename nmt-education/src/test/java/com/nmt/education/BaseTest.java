package com.nmt.education;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: PeterChen
 * @Date: 2019/2/9 19:09
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"logging.path=logs",
                "spring.profiles.active=prod",
                "eureka.client.registerWithEureka=false"
        })
@DirtiesContext
public class BaseTest {
}
