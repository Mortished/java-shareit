package ru.practicum.shareit.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ErrorHandlingControllerAdvice {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public List<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
    List<ApiError> errorList = new ArrayList<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String errorMessage = error.getDefaultMessage();
      errorList.add(new ApiError(
          Objects.requireNonNull(ex.getFieldError()).getField() + ": " + errorMessage));
    });
    return errorList;
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ApiError handleValidationExceptions(MethodArgumentTypeMismatchException ex) {
    return new ApiError("Unknown state: " + ex.getValue().toString());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException.class)
  public ApiError handleValidationExceptions(IllegalArgumentException ex) {
    return new ApiError(ex.getMessage());
  }

}
