package com.github.nutt1101.dyucoursetool.modal;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Course {
    private String courseId;
    private String courseName;
    private short credit; // 學分
}
