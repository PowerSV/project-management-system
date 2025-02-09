package ru.ballo.projects.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        // Получение списка ошибок валидации из BindingResult
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        // Создание сообщения об ошибке на основе полей и сообщений об ошибках
        StringBuilder errorMessage = new StringBuilder("Validation error");
        for (FieldError fieldError : fieldErrors) {
            errorMessage.append("\nField: ")
                    .append(fieldError.getField())
                    .append(", Message: ")
                    .append(fieldError.getDefaultMessage());
        }

        ErrorResponse errorResponse = ErrorResponse
                .builder(ex, HttpStatus.BAD_REQUEST, errorMessage.toString())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
