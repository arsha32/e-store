package com.example.ecommerce.exception;
 
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyGlobalException {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> MethodArgumentEception(MethodArgumentNotValidException e)
    {
        Map<String, String> response = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err ->{
            String fieldName=((FieldError)err).getField();
            String massage=err.getDefaultMessage();
            response.put(fieldName,massage);
        });
        return new ResponseEntity<Map<String,String>>(response, HttpStatus.BAD_REQUEST);
    }
}
