package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.info("Ошибка на стороне веб-сервера {}", e.getMessage(), e);
        return new ErrorResponse("InternalServerError", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExists(final UserAlreadyExistsException e) {
        log.warn("Ошибка. Пользователь уже существует - {}", e.getMessage());
        return new ErrorResponse("UserAlreadyExists", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final DataNotFoundException e) {
        log.warn("Ошибка. Объект не найден - {}", e.getMessage());
        return new ErrorResponse("Ошибка. Объект не найден.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleThrowable(final MethodArgumentNotValidException e) {
        log.info("Ошибка. Неверный запрос. {}", e.getMessage(), e);
        return new ErrorResponse("MethodArgumentNotValid", e.getMessage());
    }

}