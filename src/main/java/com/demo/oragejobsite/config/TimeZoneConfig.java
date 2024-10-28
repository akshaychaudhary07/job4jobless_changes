package com.demo.oragejobsite.config;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.util.TimeZone;

@Component
public class TimeZoneConfig {
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }
}
