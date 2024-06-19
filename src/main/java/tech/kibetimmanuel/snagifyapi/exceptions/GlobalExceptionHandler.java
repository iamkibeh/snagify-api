package tech.kibetimmanuel.snagifyapi.exceptions;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.lang.model.type.ErrorType;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException exception) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(
                HttpStatusCode.valueOf(exception.getStatus()).value(),
                exception.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(exception.getStatus()).body(customErrorResponse);

    }


    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityExceptions(Exception exception) {
        ProblemDetail errorDetail = null;
        // todo: send this stack trace to an observability tool
        exception.printStackTrace();

        if(exception instanceof AuthenticationException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description",  "Authorization failed. Please login to access this resource.");
        }

        if (exception instanceof BadCredentialsException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("description", "The username or password is incorrect");

        }

        if (exception instanceof AccountStatusException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The account is locked");

        }

        if (exception instanceof AccessDeniedException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "You are not allowed to access this resource");

        }

        if (exception instanceof ExpiredJwtException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("description", "The JWT token has expired");

        }

        if (exception instanceof SignatureException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("description", "The JWT signature is invalid");

        }

        if (exception instanceof MalformedJwtException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("description", "The JWT token is malformed.");
        }

        if (errorDetail == null) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
            errorDetail.setProperty("description", "Unknown internal server error");

        }

        return errorDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ValidationErrorResponseModel handleValidationException(MethodArgumentNotValidException exception) {
        List<ValidationErrorModel> validationErrors = processValidationErrors(exception);

        return ValidationErrorResponseModel.builder()
                .type("VALIDATION")
                .errors(validationErrors)
                .status(exception.getStatusCode().value())
                .build();
    }

//    DataIntegrityViolationException  handler
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        ProblemDetail errorDetail;
        if (exception.getCause() instanceof ConstraintViolationException consVioExe){
            String constraintName = consVioExe.getConstraintName();
            if (constraintName != null && constraintName.toLowerCase().contains("email")){
                errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(409), "Email already exists!");
                errorDetail.setProperty("description", "Validation error: Email should be unique.");
                return errorDetail;
            }
        }
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(409), "Data Integrity Violation");
    }

    @ExceptionHandler(ResourceNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFound exc) {
        ProblemDetail errorDetail;
        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()), "Resource Not found!");
        errorDetail.setProperty("description", exc.getMessage());
        return errorDetail;
    }

    private List<ValidationErrorModel> processValidationErrors(MethodArgumentNotValidException exception) {

        List<ValidationErrorModel> validationErrorModels = new ArrayList<>();
        for(FieldError fieldError: exception.getBindingResult().getFieldErrors()) {
            validationErrorModels.add(ValidationErrorModel.builder()
                    .code(fieldError.getCode())
                    .source(fieldError.getObjectName() + "/" + fieldError.getField())
                    .detail(fieldError.getDefaultMessage())
                    .build());
        }
        return validationErrorModels;
    }
}