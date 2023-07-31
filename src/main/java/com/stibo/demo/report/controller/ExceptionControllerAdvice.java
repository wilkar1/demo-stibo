package com.stibo.demo.report.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler
    public void handle(HttpClientErrorException ex, HttpServletResponse response) throws IOException {
        response.sendError(ex.getStatusCode().value(), ex.getStatusText());
    }
}
