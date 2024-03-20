package ru.practicum.shareit.exeption;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandlingControllerAdvice {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public List<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
    List<ApiError> errorList = new ArrayList<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String errorMessage = error.getDefaultMessage();
      errorList.add(new ApiError(errorMessage));
    });
    return errorList;
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(DuplicateUserEmailException.class)
  public ApiError handleValidationExceptions(DuplicateUserEmailException ex) {
    return new ApiError(ex.getMessage());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(UserNotFoundException.class)
  public ApiError handleValidationExceptions(UserNotFoundException ex) {
    return new ApiError(ex.getMessage());
  }

}
