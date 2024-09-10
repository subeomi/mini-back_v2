package org.gbsb.duncool.request;

import io.github.bucket4j.Bucket;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.controller.advice.BucketConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
public class BucketTests {


    private Bucket bucket;

    @BeforeEach
    void setUp() {
        BucketConfig config = new BucketConfig();
        bucket = config.bucket();
    }

    @Test
    void testRateLimit() {
        // 처음 5개 요청은 성공해야 함
        for (int i = 0; i < 5; i++) {
            assertTrue(bucket.tryConsume(1), "Request " + (i + 1) + " should be allowed");
        }

        // 6번째 요청은 실패해야 함
        assertFalse(bucket.tryConsume(1), "Request 6 should be denied");

        // 1초 대기
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 대기 후 다시 요청하면 성공해야 함
        assertTrue(bucket.tryConsume(1), "Request after waiting should be allowed");
    }
}
