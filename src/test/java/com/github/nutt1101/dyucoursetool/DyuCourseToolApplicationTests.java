package com.github.nutt1101.dyucoursetool;

import com.github.nutt1101.dyucoursetool.modal.LoginParameter;
import com.github.nutt1101.dyucoursetool.modal.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class DyuCourseToolApplicationTests {
    @Autowired
    DyuCourseBrowser browser;

    @Test
    void contextLoads() throws IOException {
        User user = User.builder()
                .loginParameter(
                        LoginParameter.builder()
                                .id("F1006019")
                                .password("Shawn1101")
                                .build()
                )
                .build();
    }

}
