package com.example.zzserver.config.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MyLogger { // 이름 변경
    private static final Logger logger = LoggerFactory.getLogger(MyLogger.class);

    public void doSomething() {
        logger.info("작업 시작");
        logger.debug("디버그용 변수: {}", 42);
        logger.warn("경고 발생");
        logger.error("에러 발생", new RuntimeException("테스트"));
    }

    // 개발용 편의 메서드 추가 가능
    public static void logDebug(String message, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, args);
        }
    }
}
