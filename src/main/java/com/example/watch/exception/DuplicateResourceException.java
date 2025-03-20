package com.example.watch.exception;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.RequestBody;

public class DuplicateResourceException extends Throwable {
    public DuplicateResourceException(@NotBlank(message = "SKU is required") String s) {
    }
}
