package com.github.nutt1101.dyucoursetool.modal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
public class LoginParameter {
    private String id;
    private String password;
    @Setter
    private LocalDateTime loginTime;
}
