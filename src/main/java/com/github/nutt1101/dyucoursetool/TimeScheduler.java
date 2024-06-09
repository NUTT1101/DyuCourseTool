package com.github.nutt1101.dyucoursetool;

import com.github.nutt1101.dyucoursetool.modal.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TimeScheduler {
    final Configuration configuration;
    final CourseAsyncService courseAsyncService;

    @Scheduled(cron = "0 * * * * *")
    void check() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        List<User> users;
        if ((users = configuration.getUserByCourseTime(now)).isEmpty()) return;

        for (User user : users) {
            this.courseAsyncService.addCourse(
                    user
            );
        }
    }
}
