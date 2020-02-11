package com.revolut.transfer.app.service;

import com.revolut.transfer.app.exception.ViolationException;
import lombok.AllArgsConstructor;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;

@AllArgsConstructor
public class ModelValidator<E> {
    protected final Validator validator;

    public void validate(E requestModel, Class<?>... groups) throws ViolationException {
        var errorMap = new HashMap<String, List<String>>();
        Set<ConstraintViolation<E>> errors = validator.validate(requestModel, groups);
        if (!errors.isEmpty()) {
            errors.forEach(e -> addToMap(e, errorMap));
            throw new ViolationException("There are some violations", errorMap);
        }
    }

    private void addToMap(ConstraintViolation e, Map<String, List<String>> errorMap) {
        var parameterName = e.getPropertyPath().toString();
        var message = e.getMessage();

        var errorList = (errorMap.containsKey(parameterName)) ? errorMap.get(parameterName) : new ArrayList<String>();

        errorList.add(message);
        errorMap.put(parameterName, errorList);
    }
}
