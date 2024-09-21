package com.ams.api.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse implements Response, Serializable {
    private Long id;
    private String token;
    private String type="Bearer";
    private String refreshToken;
    private String username;
    private String email;
    private List<String> roles;
}
