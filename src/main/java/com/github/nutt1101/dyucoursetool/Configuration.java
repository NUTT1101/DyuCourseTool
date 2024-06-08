package com.github.nutt1101.dyucoursetool;

import com.github.nutt1101.dyucoursetool.modal.Course;
import com.github.nutt1101.dyucoursetool.modal.LoginParameter;
import com.github.nutt1101.dyucoursetool.modal.User;
import com.github.nutt1101.dyucoursetool.utils.ConfigurationJsonLoader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
@Log4j2
public class Configuration {
    final ConfigurationJsonLoader configurationJsonLoader;
    final DyuCourseBrowser browser;
    List<User> users;

    @PostConstruct
    void setup() throws IOException {
        this.users = new ArrayList<>();
//        this.setupUser();
    }

    void setupUser() throws IOException {
        JSONObject jsonObj = this.configurationJsonLoader.getJsonObject();
        JSONArray userArray = jsonObj.getJSONArray("info");
        for (int i = 0; i < userArray.length(); i++) {
            StringBuilder messageBuilder = new StringBuilder();
            JSONObject user = userArray.getJSONObject(i);
            LoginParameter parameter = this.getLoginParameterBuilder(user).build();

            JSONArray courseArray = user.getJSONArray("courses");

            User newUser = this.getUserBuilder(parameter, this.getCourses(courseArray)).build();

            this.browser.login(newUser);

            messageBuilder.append("----以下是您要選的課程----").append("\n");
            newUser.getCourses().forEach(course -> {
                String message = String.format(
                        """
                        課程代號：%s
                        課程名稱：%s
                        課程學分：%s
                        ------
                        """, course.getCourseId(), course.getCourseName(), course.getCredit()
                );
                messageBuilder.append(message);
            });

            this.users.add(newUser);

            log.info(messageBuilder);
        }
    }

    LoginParameter.LoginParameterBuilder getLoginParameterBuilder(JSONObject userObject) {
        String id = userObject.getString("id");
        String pwd = userObject.getString("pwd");
        return LoginParameter.builder()
                .id(id)
                .password(pwd);
    }

    List<Course> getCourses(JSONArray courseArray) {
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < courseArray.length(); i++) {
            String courseId = courseArray.getString(i);
            try {
                courses.add(
                        this.browser.getCourse(courseId)
                );
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return courses;
    }

    User.UserBuilder getUserBuilder(LoginParameter p, List<Course> courses) {
        return User.builder()
                .loginParameter(p)
                .courses(courses)
                .headerLog(null)
                .resaveString(null);
    }
}
