package com.github.nutt1101.dyucoursetool.modal;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginParameter {
    private String id;
    private String password;
}
