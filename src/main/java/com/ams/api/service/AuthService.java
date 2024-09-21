package com.ams.api.service;

import com.ams.api.exception.NotFoundException;
import com.ams.api.exception.TokenInvalidException;
import com.ams.api.payload.request.LoginRequest;
import com.ams.api.payload.response.Response;

public interface AuthService {
    Response authenticateUser(LoginRequest loginRequest) throws TokenInvalidException, NotFoundException;

    Boolean isAuthorizedUser(String token, String url) throws TokenInvalidException;
}
