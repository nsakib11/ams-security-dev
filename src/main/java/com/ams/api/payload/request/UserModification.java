package com.ams.api.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserModification implements Serializable {

    private Long id;

    @Size(max = 45, message = "Invalid email size must be 45 characters or less")
    @NotBlank(message = "Email or Phone is Mandatory Input")
//    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message = "Invalid email format")
    @JsonProperty("emailOrPhone")
    private String emailOrPhone;

    @Size(max = 40, message = "Invalid username size must be 40 characters or less")
//    @NotBlank(message = "Username is Mandatory Input")
    @JsonProperty("username")
    private String username;

    @Size(max = 11, message = "Invalid Mobile size must be 11 characters")
//    @NotBlank(message = "Mobile is Mandatory Input")
    @Pattern(regexp = "^01[3-9]\\d{8}$", message = "Mobile number is invalid. It should be like (01XXXXXXXXX)")
    @JsonProperty("mobileNo")
    private String mobileNo;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", emailOrPhone='" + emailOrPhone + '\'' +
                ", username='" + username + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                '}';
    }
}
