package org.puumcore.customermanagement.exceptions;


import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;


/**
 * @author Puum Core (Mandela Muriithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Muriithi</a><br>
 * Project: ms-customer-management
 * @version 1.x
 * @since 9/20/2024 12:37 PM
 */

@ControllerAdvice
@Slf4j
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<String> handleCustomException(AccessDeniedException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(ex.getMessage(), httpStatus);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<String> handleCustomException(BadRequestException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(ex.getMessage(), httpStatus);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<String> handleCustomException(NotFoundException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(ex.getMessage(), httpStatus);
    }

    @ExceptionHandler({FailureException.class})
    public ResponseEntity<String> handleCustomException(FailureException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(ex.getMessage(), httpStatus);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUncaughtExceptions(@NonNull Exception ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        log.error("Uncaught error thrown", ex);

        String msg = "We ran into a problem handling your request. Check logs for more info";
        Throwable throwable = Optional.ofNullable(ex.getCause()).orElse(ex);
        if (throwable instanceof HttpMessageNotReadableException || throwable instanceof NullPointerException) {
            msg = throwable.getMessage();
        }
        return new ResponseEntity<>(msg, httpStatus);
    }


}
