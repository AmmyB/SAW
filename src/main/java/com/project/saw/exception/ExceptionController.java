package com.project.saw.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler({DuplicateException.class})
    public ResponseEntity<Object> handle(DuplicateException de, WebRequest request){
        return ResponseEntity.badRequest().body(de.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handle(MethodArgumentNotValidException manve, WebRequest request){
        String customException = manve.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getField() + " - " + x.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return  ResponseEntity.badRequest().body(customException);
    }
}
