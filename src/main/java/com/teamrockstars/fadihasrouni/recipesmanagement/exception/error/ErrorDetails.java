package com.teamrockstars.fadihasrouni.recipesmanagement.exception.error;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorDetails {

    private Date timestamp;
    private String message;
    private Map<String, String> errorMap;
    private String details;

    public ErrorDetails(Date timestamp, String message, String details) {
        super();
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

}
