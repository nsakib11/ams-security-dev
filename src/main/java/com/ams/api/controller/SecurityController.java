package com.ams.api.controller;

import com.ams.api.exception.NotFoundException;
import com.ams.api.exception.RegistrationException;
import com.ams.api.exception.TokenInvalidException;
import com.ams.api.jwt.JwtUtils;
import com.ams.api.payload.request.LoginRequest;
import com.ams.api.payload.request.RegistrationGetRequest;
import com.ams.api.payload.request.RegistrationModificationRequest;
import com.ams.api.payload.request.RegistrationRequest;
import com.ams.api.payload.response.RegistrationGetResponse;
import com.ams.api.service.AuthService;
import com.ams.api.service.RegistrationService;
import com.ams.api.model.CustomResponse;
import com.ams.api.payload.response.Response;
import com.ams.api.service.TokenService;
import com.ams.api.util.GlobalConstant;
import com.ams.api.util.ResponseBuilder;
import com.ams.api.util.SessionMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;

import static com.ams.api.util.GlobalConstant.KEY_MAP_API_VERSION;


@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/ams/api/")
@Validated
public class SecurityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityController.class);
    @Autowired
    RegistrationService registrationService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    ResponseBuilder<Response> responseBuilder;

    @Autowired
    TokenService tokenService;
    @Autowired
    AuthService authService;

    @PostMapping(value = "{version}/register")
    public ResponseEntity<CustomResponse<Response>> register(
            @PathVariable("version") String version,
            @Valid @RequestBody RegistrationRequest requestBody,
            HttpServletRequest httpServletRequest
    ) throws RegistrationException {
        LOGGER.info("Entered in method-register of class-SecurityController at {}", System.currentTimeMillis());
        LOGGER.info("Request received for register {}", requestBody);
        SessionMap.setContext(KEY_MAP_API_VERSION, version);

        Response vo = registrationService.register(requestBody);

        CustomResponse<Response> response = new CustomResponse<>();
        response.setResponse(Collections.singletonList(vo));
        response.setErrors(Collections.emptyList());
        return ResponseEntity.ok().body(responseBuilder.buildResponse(response));
    }

    @PostMapping("{version}/auth/signin")
    public ResponseEntity<CustomResponse<Response>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, @PathVariable String version) throws TokenInvalidException, NotFoundException {
        LOGGER.info("Entered in method-register of class-SecurityController at {}", System.currentTimeMillis());
        LOGGER.info("Request received for authenticateUser {}", loginRequest);

        Response responseVo = authService.authenticateUser(loginRequest);
        CustomResponse<Response> response = new CustomResponse<>();
        response.setResponse(Collections.singletonList(responseVo));
        response.setErrors(Collections.emptyList());
        response.setTraceId(String.valueOf(SessionMap.getValue(GlobalConstant.KEY_MAP_TRACE_ID)));
        response.setTimeStamp(LocalDateTime.now().toString());
        response.setSuccess(true);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("{version}/register-info")
    public ResponseEntity<CustomResponse<Response>> getRegisterInfo(@Valid @RequestBody RegistrationGetRequest registrationGetRequest, @PathVariable String version) throws TokenInvalidException, NotFoundException, RegistrationException {
        LOGGER.info("Entered in method-register of class-SecurityController at {}", System.currentTimeMillis());
        LOGGER.info("Request received for authenticateUser {}", registrationGetRequest);

        Response responseVo = registrationService.getRegistrationInfo(registrationGetRequest.getEmailOrPhone());
        CustomResponse<Response> response = new CustomResponse<>();
        response.setResponse(Collections.singletonList(responseVo));
        response.setErrors(Collections.emptyList());
        response.setTraceId(String.valueOf(SessionMap.getValue(GlobalConstant.KEY_MAP_TRACE_ID)));
        response.setTimeStamp(LocalDateTime.now().toString());
        response.setSuccess(true);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value = "{version}/register-info")
    public ResponseEntity<CustomResponse<Response>> updateRegisterInfo(
            @PathVariable("version") String version,
            @Valid @RequestBody RegistrationModificationRequest requestBody,
            HttpServletRequest httpServletRequest
    ) throws RegistrationException {
        LOGGER.info("Entered in method-register of class-SecurityController at {}", System.currentTimeMillis());
        LOGGER.info("Request received for register {}", requestBody);
        SessionMap.setContext(KEY_MAP_API_VERSION, version);

        Response vo = registrationService.updateRegistrationInfo(requestBody);

        CustomResponse<Response> response = new CustomResponse<>();
        response.setResponse(Collections.singletonList(vo));
        response.setErrors(Collections.emptyList());
        return ResponseEntity.ok().body(responseBuilder.buildResponse(response));
    }

}
