package com.github.nutt1101.dyucoursetool;

import com.github.nutt1101.dyucoursetool.modal.Course;
import com.github.nutt1101.dyucoursetool.modal.LoginParameter;
import com.github.nutt1101.dyucoursetool.modal.User;
import com.github.nutt1101.dyucoursetool.utils.ConfigurationJsonLoader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
public class Configuration {
    final ConfigurationJsonLoader configurationJsonLoader;
    final DyuCourseBrowser browser;
    List<User> users;

    @PostConstruct
    void setup() {
        this.users = new ArrayList<>();
        setupUser();
    }

    void setupUser() {
        JSONObject jsonObj = this.configurationJsonLoader.getJsonObject();
        JSONArray userArray = jsonObj.getJSONArray("info");
        for (int i = 0; i < userArray.length(); i++) {
            JSONObject user = userArray.getJSONObject(i);
            LoginParameter parameter = this.getLoginParameterBuilder(user).build();

            JSONArray courseArray = user.getJSONArray("courses");
            List<Course> courses = this.getCourses(courseArray);

            User newUser = this.getUserBuilder(parameter, courses).build();

            this.users.add(newUser);
            try {
                browser.login(users.get(0));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
                browser.getCourse(courseId);
            } catch (IOException e) {
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
