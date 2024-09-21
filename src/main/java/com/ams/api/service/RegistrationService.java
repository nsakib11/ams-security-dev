package com.ams.api.service;


import com.ams.api.exception.RegistrationException;
import com.ams.api.payload.request.RegistrationModificationRequest;
import com.ams.api.payload.request.RegistrationRequest;
import com.ams.api.payload.response.Response;

public interface RegistrationService {

    Response register(RegistrationRequest requestBody) throws RegistrationException;
    Response getRegistrationInfo(String emailOrPhone) throws RegistrationException;

    Response updateRegistrationInfo(RegistrationModificationRequest registrationRequest) throws RegistrationException;

}
