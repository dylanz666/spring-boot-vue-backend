package com.github.dylanz666.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author : dylanz
 * @since : 10/04/2020
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Component
public class SignInResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String userType;
    private String message;
    private String status;
    private int code;
}