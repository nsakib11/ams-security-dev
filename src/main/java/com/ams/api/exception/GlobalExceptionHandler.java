package com.ams.api.exception;

import com.ams.api.model.CustomError;
import com.ams.api.model.CustomPayload;
import com.ams.api.model.CustomResponse;
import com.ams.api.util.ResponseBuilder;
import com.ams.api.util.SessionMap;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static com.ams.api.util.GlobalConstant.*;

@ControllerAdvice
public class GlobalExceptionHandler{

	@Autowired
	private ResponseBuilder<CustomPayload> responseBuilder;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ MethodArgumentNotValidException.class})
	public ResponseEntity handleMethodArgumentNotValid(HttpServletRequest request, MethodArgumentNotValidException ex) {

		this.logger.info("GlobalExceptionHandler Exception request :" + request);
		this.logger.error("Error Track is:::-------", ex);

		FieldError fieldError = ex.getBindingResult().getFieldError();
		logger.error("[handleMethodArgumentNotValid]-field={}, rejectedValue={}, defaultMessage={}",
				fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage());

		CustomError error = new CustomError();
		if (ObjectUtils.isEmpty(fieldError.getRejectedValue())) {
			error.setCode(MANDATORY_FIELD_WARNING_CODE)
					.setMessage(String.format("%s : %s",fieldError.getField(), fieldError.getDefaultMessage()))
					.setType(MANDATORY_FIELD_WARNING_TYPE);
		} else {
			error.setCode(FORMAT_ERROR_WARNING_CODE)
					.setMessage(String.format("%s : %s",fieldError.getField(), fieldError.getDefaultMessage()))
					.setType(FORMAT_ERROR_WARNING_TYPE);
		}
		List<CustomError> errors = new ArrayList<>();
		errors.add(error);
		this.logger.error(ex.getMessage());

		String traceId = String.valueOf(SessionMap.getValue(KEY_MAP_TRACE_ID));
		CustomResponse response = responseBuilder.buildResponse(Collections.emptyList(), errors, traceId, false);
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler({ SQLException.class, DataIntegrityViolationException.class })
	public ResponseEntity<CustomResponse<CustomPayload>> handleSQLException(HttpServletRequest request, Exception ex) {
		this.logger.info("GlobalExceptionHandler HttpServletRequest request :" + request);
		this.logger.error("Error Track is:::-------", ex);

		CustomError error = new CustomError();
		error.setCode(SOMETHING_WRONG_ERROR_CODE).setMessage(SOMETHING_WRONG_ERROR_MESSAGE)
				.setType(SOMETHING_WRONG_ERROR_TYPE);

		List<CustomError> errors = new ArrayList<>();
		errors.add(error);

		String traceId = String.valueOf(SessionMap.getValue(KEY_MAP_TRACE_ID));

		this.logger.error("There was an Error trying to read/write to the database:: SQL Exception");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(this.responseBuilder.buildResponse(Collections.emptyList(), errors, traceId, false));
	}

	@ExceptionHandler({InvalidFormatException.class})
	public ResponseEntity<CustomResponse<CustomPayload>> handleInvalidFormatException(HttpServletRequest request, InvalidFormatException ex) {
		this.logger.info("GlobalExceptionHandler HttpServletRequest request :" + request);
		this.logger.error("Error Track is:::-------", ex);

		CustomError error = new CustomError();
		error.setCode(FORMAT_ERROR_WARNING_CODE).setMessage(FORMAT_ERROR_WARNING_MESSAGE)
				.setType(FORMAT_ERROR_WARNING_TYPE);

		List<CustomError> errors = new ArrayList<>();
		errors.add(error);

		String traceId = String.valueOf(SessionMap.getValue(KEY_MAP_TRACE_ID));

		this.logger.error("There was an Error in the format of the value provided");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(this.responseBuilder.buildResponse(Collections.emptyList(), errors, traceId, false));
	}

	@ExceptionHandler({ EmptyResultDataAccessException.class, NoSuchElementException.class,
			NullPointerException.class })
	public ResponseEntity<CustomResponse<CustomPayload>> handleEmptyResultException(HttpServletRequest request,
			Exception ex) {
		this.logger.info("GlobalExceptionHandler HttpServletRequest request :" + request);
		this.logger.error("Error Track is:::-------", ex);

		CustomError error = new CustomError();
		error.setCode(SOMETHING_WRONG_ERROR_CODE).setMessage(SOMETHING_WRONG_ERROR_MESSAGE)
				.setType(SOMETHING_WRONG_ERROR_TYPE);
		List<CustomError> errors = new ArrayList<>();
		errors.add(error);

		String traceId = String.valueOf(SessionMap.getValue(KEY_MAP_TRACE_ID));

		this.logger.error("There was an Error trying to retrieve the data");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(this.responseBuilder.buildResponse(Collections.emptyList(), errors, traceId, false));
	}

	@ExceptionHandler({ NGIBaseException.class })
	public ResponseEntity<CustomResponse<CustomPayload>> handleCustomException(HttpServletRequest request,
			NGIBaseException ex) {
		this.logger.info("GlobalExceptionHandler HttpServletRequest request :" + request);
		this.logger.error("Error Track is:::-------", ex);

		String traceId = String.valueOf(SessionMap.getValue(KEY_MAP_TRACE_ID));

		this.logger.error("There was an Error trying to retrieve the data");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				this.responseBuilder.buildResponse(Collections.emptyList(), ex.getErrors(), traceId, false));
	}

	@ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<CustomResponse<CustomPayload>> handleBadCredentialsException(HttpServletRequest request, BadCredentialsException ex) {
		this.logger.info("GlobalExceptionHandler Exception request :" + request);
		this.logger.error("Error Track is:::-------", ex);

		CustomError error = new CustomError();
		error.setCode(BAD_CRED_CODE).setMessage(BAD_CRED_MESSAGE)
				.setType(HttpStatus.UNAUTHORIZED.getReasonPhrase());
		List<CustomError> errors = new ArrayList<>();
		errors.add(error);
		this.logger.error(ex.getMessage());

		String traceId = String.valueOf(SessionMap.getValue(KEY_MAP_TRACE_ID));

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(this.responseBuilder.buildResponse(Collections.emptyList(), errors, traceId, false));
	}

	@ExceptionHandler({ Exception.class, RuntimeException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<CustomResponse<CustomPayload>> handleCustomException(HttpServletRequest request,
			Exception ex) {
		this.logger.info("GlobalExceptionHandler Exception request :" + request);
		this.logger.error("Error Track is:::-------", ex);

		CustomError error = new CustomError();
		error.setCode(SOMETHING_WRONG_ERROR_CODE).setMessage(SOMETHING_WRONG_ERROR_MESSAGE)
				.setType(SOMETHING_WRONG_ERROR_TYPE);
		List<CustomError> errors = new ArrayList<>();
		errors.add(error);
		this.logger.error(ex.getMessage());

		String traceId = String.valueOf(SessionMap.getValue(KEY_MAP_TRACE_ID));

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(this.responseBuilder.buildResponse(Collections.emptyList(), errors, traceId, false));
	}
	@ExceptionHandler({GLException.class})
	public ResponseEntity<CustomResponse<CustomPayload>> handleGlException(HttpServletRequest request, GLException ex) {
		this.logger.info("GlobalExceptionHandler HttpServletRequest request :" + request);
		this.logger.error("Error Track is:::-------", ex);

		List<CustomError> errors = ex.getErrors();
		if(errors == null || errors.size() < 1){
			CustomError error = new CustomError();
			error.setCode(INVALID_FIELD_MANDATORY_ERROR_CODE).setMessage(ex.getMessage())
					.setType(INVALID_FIELD_MANDATORY_ERROR_TYPE);

			errors = new ArrayList<>();
			errors.add(error);
		}

		String traceId = String.valueOf(SessionMap.getValue(KEY_MAP_TRACE_ID));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(this.responseBuilder.buildResponse(Collections.emptyList(), errors, traceId, false));
	}
	@ExceptionHandler({GlobalException.class})
	public ResponseEntity<CustomResponse<CustomPayload>> handleGlobaleException(HttpServletRequest request, GlobalException ex) {
		this.logger.info("GlobalExceptionHandler HttpServletRequest request :" + request);
		this.logger.error("Error Track is:::-------", ex);

		List<CustomError> errors = ex.getErrors();
		if(errors == null || errors.size() < 1){
			CustomError error = new CustomError();
			error.setCode(INVALID_FIELD_MANDATORY_ERROR_CODE).setMessage(ex.getMessage())
					.setType(INVALID_FIELD_MANDATORY_ERROR_TYPE);
			errors = new ArrayList<>();
			errors.add(error);
		}

		String traceId = String.valueOf(SessionMap.getValue(KEY_MAP_TRACE_ID));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(this.responseBuilder.buildResponse(Collections.emptyList(), errors, traceId, false));
	}

	@ExceptionHandler({ConstraintViolationException.class})
	public ResponseEntity<CustomResponse<CustomPayload>> handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException ex) {
		this.logger.info("GlobalExceptionHandler HttpServletRequest request :" + request);
		this.logger.error("Error Track is:::-------", ex);

		CustomError error = new CustomError();
		error.setCode(FORMAT_ERROR_WARNING_CODE).setMessage(ex.getMessage())
				.setType(FORMAT_ERROR_WARNING_TYPE);
		List<CustomError> errors = new ArrayList<>();
		errors.add(error);
		this.logger.error(ex.getMessage());
		String traceId = String.valueOf(SessionMap.getValue(KEY_MAP_TRACE_ID));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(this.responseBuilder.buildResponse(Collections.emptyList(), errors, traceId, false));
	}

	@ExceptionHandler({HttpMessageNotReadableException.class})
	public ResponseEntity<CustomResponse<CustomPayload>> handleJsonMappingException(HttpServletRequest request, HttpMessageNotReadableException ex) {


			this.logger.info("GlobalExceptionHandler Exception request: " + request);
		this.logger.error("Error HttpMessageNotReadableException Track: ", ex);

		String conciseErrorMessage = "Invalid data received. ";

		// Extract field name from the exception message, assuming the message format contains field information
		String errorMessage = ex.getMessage();
		String fieldName = null;

		if (errorMessage != null) {
			int fieldNameStartIndex = errorMessage.indexOf("[\"");
			int fieldNameEndIndex = errorMessage.indexOf("\"]", fieldNameStartIndex + 1);

			if (fieldNameStartIndex != -1 && fieldNameEndIndex != -1) {
				fieldName = errorMessage.substring(fieldNameStartIndex + 2, fieldNameEndIndex);
			}
		}

		if (fieldName != null) {
			conciseErrorMessage += "Invalid data in field: " + fieldName + ". Please check and correct the field value.";
		} else {
			conciseErrorMessage += "Please check the request body and ensure it is formatted correctly.";
		}

		CustomError error = new CustomError();
		error.setCode(FORMAT_ERROR_WARNING_CODE)
				.setMessage(conciseErrorMessage)
				.setType(FORMAT_ERROR_WARNING_TYPE);

		List<CustomError> errors = Collections.singletonList(error);

		String traceId = String.valueOf(SessionMap.getValue(KEY_MAP_TRACE_ID));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(this.responseBuilder.buildResponse(Collections.emptyList(), errors, traceId, false));
	}
	@ExceptionHandler({JsonMappingException.class})
	public ResponseEntity<CustomResponse<CustomPayload>> handleJsonMappingException(HttpServletRequest request, JsonMappingException ex) {
		this.logger.info("Json Mapping Exception request :" + request);
		this.logger.error("Error Track is:::-------", ex);

		CustomError error = new CustomError();
		error.setCode(FORMAT_ERROR_WARNING_CODE).setMessage(ex.getMessage())
				.setType(FORMAT_ERROR_WARNING_TYPE);
		List<CustomError> errors = new ArrayList<>();
		errors.add(error);
		this.logger.error(ex.getMessage());
		String traceId = String.valueOf(SessionMap.getValue(KEY_MAP_TRACE_ID));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(this.responseBuilder.buildResponse(Collections.emptyList(), errors, traceId, false));
	}

	@ExceptionHandler({ RegistrationException.class })
	public ResponseEntity<CustomResponse<CustomPayload>> handleRegistrationException(HttpServletRequest request,
																					 RegistrationException ex) {
		this.logger.info("RegistrationException HttpServletRequest request :" + request);
		this.logger.error("Error Track is:::-------", ex);

		String traceId = String.valueOf(SessionMap.getValue(KEY_MAP_TRACE_ID));
		CustomError error = new CustomError();
		error.setCode(FORMAT_ERROR_WARNING_CODE).setMessage(ex.getMessage())
				.setType(FORMAT_ERROR_WARNING_TYPE);
		List<CustomError> errors = new ArrayList<>();
		errors.add(error);
		this.logger.error("There was an Error trying to retrieve the data");
		return ResponseEntity.badRequest().body(
				this.responseBuilder.buildResponse(Collections.emptyList(), errors, traceId, false));
	}

}