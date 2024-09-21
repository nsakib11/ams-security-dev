package com.ams.api.payload.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationModificationRequest implements Serializable {

    @NotNull
    @Valid
    private UserModification user;

    @Override
    public String toString() {
        return "RegistrationModificationRequest{" +
                "userModification=" + user +
                '}';
    }
}
