package com.github.nutt1101.dyucoursetool.modal;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@ToString
public class User {
    LoginParameter loginParameter;
    List<Course> courses;
    String headerLog;
    String serviceHost;
    LocalDateTime timeToAddCourse;
}
