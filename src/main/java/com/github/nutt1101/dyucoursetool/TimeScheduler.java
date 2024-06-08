package com.github.nutt1101.dyucoursetool;

import com.github.nutt1101.dyucoursetool.modal.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TimeScheduler {
    final Configuration configuration;
    final CourseAsyncService courseAsyncService;

    @PostConstruct // TODO : cron
    void check() throws IOException {
        // TODO : time checker
        
        for (User user : configuration.users) {
            this.courseAsyncService.addCourse(
                    user
            ); // Async method
        }
    }
}
