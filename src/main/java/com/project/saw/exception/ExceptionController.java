package com.project.saw.exception;


import com.project.saw.event.EventEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler({DuplicateException.class})
    public ResponseEntity<Object> handle(DuplicateException de, WebRequest request){
        return ResponseEntity.badRequest().body(de.getMessage());
    }


}
