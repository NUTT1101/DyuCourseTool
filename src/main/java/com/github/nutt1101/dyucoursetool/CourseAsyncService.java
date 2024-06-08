package com.github.nutt1101.dyucoursetool;

import com.github.nutt1101.dyucoursetool.modal.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CourseAsyncService {
    final DyuCourseBrowser browser;

    int i =0 ;
    @Async
    void addCourse(User user) throws IOException {
        this.browser.addCourse(user);
    }
}
