package com.ams.api.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserClaim {
    private Long id;
    private String username;
    private String email;

    public UserClaim(Map<String, String> userDetailsMap) {
        this.id = Long.valueOf(String.valueOf(userDetailsMap.get("id")));
        this.username = userDetailsMap.get("username");
        this.email = userDetailsMap.get("email");
    }
}
