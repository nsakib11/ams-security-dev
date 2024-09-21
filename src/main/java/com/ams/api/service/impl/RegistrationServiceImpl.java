package com.ams.api.service.impl;

import com.ams.api.entity.User;
import com.ams.api.exception.RegistrationException;
import com.ams.api.payload.request.RegistrationModificationRequest;
import com.ams.api.payload.request.RegistrationRequest;
import com.ams.api.payload.response.RegistrationGetResponse;
import com.ams.api.payload.response.RegistrationResponse;
import com.ams.api.repository.UserRepository;
import com.ams.api.service.RegistrationService;
import com.ams.api.payload.response.Response;
import com.ams.api.util.GlobalConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.time.LocalDateTime;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public Response register(RegistrationRequest requestBody) throws RegistrationException {
        RegistrationResponse response = new RegistrationResponse();
        User registeredUser = null;
        try {
            User user = User.builder()
                    .email(requestBody.getUser().getEmailOrPhone())
                    .mobileNo(requestBody.getUser().getMobileNo())
                    .username(requestBody.getUser().getUsername())
                    .passwordHash(encoder.encode(requestBody.getUser().getPassword())) // pass need to be hased
                    .role("USER")
                    .status("REGISTERED")
                    .registeredOn(LocalDateTime.now())
                    .build();

            registeredUser = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            LOGGER.error("An DataIntegrityViolationException occurred during registration", e);
            if(e.getMessage() != null && !e.getMessage().isEmpty()){
                throw new RegistrationException(e.getMessage(), e.getCause());
            }
        } catch (DataAccessException e) {
            LOGGER.error("An DataAccessException occurred during registration", e);
            if(e.getMessage() != null && !e.getMessage().isEmpty()){
                throw new RegistrationException(e.getMessage(), e.getCause());
            }
        }
        response.setUser(com.ams.api.payload.request.User.builder()
                        .id(registeredUser.getId())
                        .emailOrPhone(registeredUser.getEmail())
                        .mobileNo(registeredUser.getMobileNo())
                        .password("xxxxxxxx")
                        .username(registeredUser.getUsername())
                .build());
        response.setResponseCode(GlobalConstant.SUCCESS_CODE);
        response.setResponseMessage(GlobalConstant.SUCCESS_MESSAGE);
        return response;
    }

    @Override
    public Response getRegistrationInfo(String emailOrPhone) throws RegistrationException {
        RegistrationGetResponse response = new RegistrationGetResponse();
        User registeredUser = null;
        try {
            registeredUser = userRepository.findByEmail(emailOrPhone).orElseThrow(() -> new NotFoundException("User not found with this : " + emailOrPhone)) ;

        } catch (DataIntegrityViolationException e) {
            LOGGER.error("An DataIntegrityViolationException occurred during registration", e);
            if(e.getMessage() != null && !e.getMessage().isEmpty()){
                throw new RegistrationException(e.getMessage(), e.getCause());
            }
        } catch (DataAccessException e) {
            LOGGER.error("An DataAccessException occurred during registration", e);
            if(e.getMessage() != null && !e.getMessage().isEmpty()){
                throw new RegistrationException(e.getMessage(), e.getCause());
            }
        }
        response.setUser(com.ams.api.payload.request.User.builder()
                .id(registeredUser.getId())
                .emailOrPhone(registeredUser.getEmail())
                .password("xxxxxxxx")
                .username(registeredUser.getUsername())
                .build());
        response.setResponseCode(GlobalConstant.SUCCESS_CODE);
        response.setResponseMessage(GlobalConstant.SUCCESS_MESSAGE);
        return response;
    }

    @Override
    public Response updateRegistrationInfo(RegistrationModificationRequest requestBody) throws RegistrationException {
        RegistrationResponse response = new RegistrationResponse();
        User updatedUser = null;
        try {
            User user = userRepository.findByEmail(requestBody.getUser().getEmailOrPhone()).orElseThrow(() -> new NotFoundException("User not found with this : " + requestBody.getUser().getEmailOrPhone()));
            user.setUsername(requestBody.getUser().getUsername());
            user.setMobileNo(requestBody.getUser().getMobileNo());
            updatedUser = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            LOGGER.error("An DataIntegrityViolationException occurred during registration", e);
            if(e.getMessage() != null && !e.getMessage().isEmpty()){
                throw new RegistrationException(e.getMessage(), e.getCause());
            }
        } catch (DataAccessException e) {
            LOGGER.error("An DataAccessException occurred during registration", e);
            if(e.getMessage() != null && !e.getMessage().isEmpty()){
                throw new RegistrationException(e.getMessage(), e.getCause());
            }
        }
        response.setUser(com.ams.api.payload.request.User.builder()
                .id(updatedUser.getId())
                .emailOrPhone(updatedUser.getEmail())
                .mobileNo(updatedUser.getMobileNo())
                .password("xxxxxxxx")
                .username(updatedUser.getUsername())
                .build());
        response.setResponseCode(GlobalConstant.SUCCESS_CODE);
        response.setResponseMessage(GlobalConstant.SUCCESS_MESSAGE);
        return response;
    }
}
