package com.capstoneproject.ElitesTracker.exceptions;

public class AttendanceAlreadyTakenException extends RuntimeException{
    public AttendanceAlreadyTakenException(String message){
        super(message);
    }
}
