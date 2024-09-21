package com.ams.api.service.impl;

import com.ams.api.ase.AesCbcUtil;
import com.ams.api.controller.SecurityController;
import com.ams.api.entity.Token;
import com.ams.api.exception.NotFoundException;
import com.ams.api.exception.TokenInvalidException;
import com.ams.api.jwt.JwtUtils;
import com.ams.api.jwt.UserClaim;
import com.ams.api.model.CustomError;
import com.ams.api.payload.request.LoginRequest;
import com.ams.api.payload.response.LoginResponse;
import com.ams.api.payload.response.Response;
import com.ams.api.service.AuthService;
import com.ams.api.service.TokenService;
import com.ams.api.util.AuthConstant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    TokenService tokenService;
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AesCbcUtil aesCbcUtil;

    @Override
    public Response authenticateUser(LoginRequest loginRequest) throws TokenInvalidException, NotFoundException {

        LOGGER.info("Password is  {}", aesCbcUtil.decrypt(loginRequest.getPassword()));
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), aesCbcUtil.decrypt(loginRequest.getPassword())));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());

        Token token = tokenService.findByUser(userDetails.getId());
        if(token == null){
            token = tokenService.saveToken(userDetails);
        }

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(userDetails.getId());
        loginResponse.setUsername(userDetails.getUsername());
        loginResponse.setEmail(userDetails.getEmail());
        loginResponse.setRoles(roles);
        loginResponse.setToken(token.getToken());
        loginResponse.setRefreshToken(token.getRefreshToken());
        return loginResponse;
    }

    @Override
    public Boolean isAuthorizedUser(String token, String url) throws TokenInvalidException {
        CustomError error = new CustomError();
        if (!jwtUtils.validateJwtToken(token)) {
            error.setCode(AuthConstant.INVALID_TOKEN_ERROR_CODE).setMessage(AuthConstant.INVALID_TOKEN_ERROR_MESSAGE)
                    .setType(AuthConstant.INVALID_TOKEN_ERROR_TYPE);
            throw new TokenInvalidException(Collections.singletonList(error), "Invalid token");
        }
        UserClaim userClaim = jwtUtils.getUserClaimFromJwtToken(token);
        try {
            Token tokenByUserId = tokenService.getTokenByJwtToken(userClaim.getId(), token);

            if (!token.equals(tokenByUserId.getToken())) {
                error.setCode(AuthConstant.INVALID_TOKEN_ERROR_CODE).setMessage(AuthConstant.INVALID_TOKEN_ERROR_MESSAGE)
                        .setType(AuthConstant.INVALID_TOKEN_ERROR_TYPE);
                throw new TokenInvalidException(Collections.singletonList(error), "Invalid token");
            }

        } catch (NotFoundException e) {
            error.setCode(AuthConstant.INVALID_TOKEN_ERROR_CODE).setMessage(AuthConstant.INVALID_TOKEN_ERROR_MESSAGE)
                    .setType(AuthConstant.INVALID_TOKEN_ERROR_TYPE);
            throw new TokenInvalidException(Collections.singletonList(error), "Invalid token");
        }
        return true;
    }



}
