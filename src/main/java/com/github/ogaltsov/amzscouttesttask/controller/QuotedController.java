package com.github.ogaltsov.amzscouttesttask.controller;

import com.github.ogaltsov.amzscouttesttask.configuration.annotation.Quoted;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/quote")
public class QuotedController {

    @Quoted
    @GetMapping
    public ResponseEntity<Object> quotedGet(HttpServletRequest request) {
        return ResponseEntity.ok().build();
    }
}
