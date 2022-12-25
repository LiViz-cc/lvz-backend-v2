package com.liviz.v2.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Utils {
    @Bean
    public Log getLogger() {
        return LogFactory.getLog(getClass());
    }

}
