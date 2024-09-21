package com.ams.api.util;

import org.springframework.http.HttpStatus;

public final class AuthConstant {


    // Failed
    public static final String AES_ENCRYPTION_FAILED_MESSAGE = "AES encryption failed.";
    public static final String AES_ENCRYPTION_FAILED_CODE = "ERR5100";

    public static final String AES_DECRYPTION_FAILED_MESSAGE = "AES decryption failed.";
    public static final String AES_DECRYPTION_FAILED_CODE = "ERR5101";

    public static final String NOT_FOUND_ERROR_CODE = "ERR1006";
    public static final String NOT_FOUND_ERROR_MESSAGE = "The requested resource was not found.";
    public static final String NOT_FOUND_ERROR_TYPE = HttpStatus.BAD_REQUEST.getReasonPhrase();
    public static final String SOMETHING_WRONG_ERROR_CODE = "ERR50001";
    public static final String SOMETHING_WRONG_ERROR_MESSAGE = "Something went wrong, please try again later";
    public static final String SOMETHING_WRONG_ERROR_TYPE = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
    public static final String GENERAL_ERROR_CODE = "ERR50000";
    public static final String GENERAL_ERROR_MESSAGE = "General Error";
    public static final String GENERAL_ERROR_TYPE = "General";
    public static final String INVALID_TOKEN_ERROR_CODE = "ERR40101";
    public static final String INVALID_TOKEN_ERROR_MESSAGE = "Token found in request is invalid.";
    public static final String INVALID_TOKEN_ERROR_TYPE = HttpStatus.BAD_REQUEST.getReasonPhrase();
    public static final String INVALID_ENCRYPTED_PASSWORD_ERROR_CODE = "ERR5621";
    public static final String INVALID_ENCRYPTED_PASSWORD_ERROR_MESSAGE = "Invalid Password";
    public static final String INVALID_ENCRYPTED_PASSWORD_ERROR_TYPE = HttpStatus.BAD_REQUEST.getReasonPhrase();
    public static final String INVALID_USER_ERROR_CODE = "ERR5619";
    public static final String INVALID_USER_ERROR_MESSAGE = "User is not found";
    public static final String INVALID_USER_ERROR_TYPE = HttpStatus.BAD_REQUEST.getReasonPhrase();
    public static final String INVALID_FIELD_NAME_ERROR_CODE = "ERR40001";
    public static final String INVALID_FIELD_NAME_ERROR_MESSAGE = "Invalid Field Name";
    public static final String INVALID_FIELD_NAME_ERROR_TYPE = HttpStatus.BAD_REQUEST.getReasonPhrase();
    public static final String USER_NOT_ALLOWED_ERROR_CODE = "ERR40301";
    public static final String USER_NOT_ALLOWED_ERROR_MESSAGE = "Feature Not Allowed";
    public static final String USER_NOT_ALLOWED_ERROR_TYPE = HttpStatus.UNAUTHORIZED.getReasonPhrase();


}