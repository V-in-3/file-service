package com.example.file.service.web.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonInclude(NON_EMPTY)
@JsonPropertyOrder({"success", "error"})
@Data
public class ApiErrorMessage implements Serializable {
    private final Boolean success;
    private final String error;
}