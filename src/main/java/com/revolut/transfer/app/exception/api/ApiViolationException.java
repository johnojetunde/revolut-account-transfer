package com.revolut.transfer.app.exception.api;

import com.revolut.transfer.app.exception.ViolationException;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class ApiViolationException extends RuntimeException {
    @Getter
    private final Map<String, List<String>> violations;

    public ApiViolationException(ViolationException cause) {
        super(cause);
        this.violations = cause.getViolations();
    }
}
