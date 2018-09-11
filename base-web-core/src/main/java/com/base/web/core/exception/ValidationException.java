package com.base.web.core.exception;

import com.base.web.bean.validator.ValidateResults;

/**
 * Created by   on 16-5-12.
 */
public class ValidationException extends BusinessException {
    private ValidateResults results;

    public ValidationException(String message) {
        super(message, 400);
    }

    public ValidationException(ValidateResults results) {
        super(results.toString(), 400);
        this.results = results;
    }

    public ValidateResults getResults() {
        return results;
    }
}
