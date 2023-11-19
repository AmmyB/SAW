package com.project.saw.exception;

public class EmailExistsException extends Throwable{

    public EmailExistsException(final String message){
        super(message);
    }
}
