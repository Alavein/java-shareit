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
        return new ErrorResponse("Ошибка на стороне веб-сервера", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExists(final UserAlreadyExistsException e) {
        log.warn("Ошибка. Пользователь уже существует - {}", e.getMessage());
        return new ErrorResponse("Ошибка. Пользователь уже существует", e.getMessage());
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
        return new ErrorResponse("шибка. Неверный запрос", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final BadRequestException e) {
        log.info("Ошибка. Неверный запрос. {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), "Ошибка. Неверный запрос.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingNotAvailable(final BookingNotAvailableException e) {
        log.info("Ошибка. Бронирование недоступно. {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка. Бронирование недоступно.", e.getMessage());
    }
}