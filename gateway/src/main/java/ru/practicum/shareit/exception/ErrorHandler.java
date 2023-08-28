package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse errorIllegalArgumentException(final IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}", e.getMessage(), e);

        return new ErrorResponse(
                e.getMessage()
        );
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse errorThrowableException(final MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException. {}", e.getMessage(), e);

        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse errorThrowableException(final Throwable e) {
        log.error("Internal Server Error. {}", e.getMessage(), e);

        return new ErrorResponse(
                e.getMessage()
        );
    }
}
