package com.revolut.transfer.app.service;

import com.revolut.transfer.app.exception.ViolationException;
import com.revolut.transfer.app.factory.ValidatorFactory;
import com.revolut.transfer.app.model.AccountRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Validator;
import java.math.BigDecimal;

import static com.revolut.transfer.app.fixture.AccountRequestModelFixture.requestModelFixture;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ModelValidatorTest {
    private ModelValidator<AccountRequestModel> modelValidator;

    @BeforeEach
    void setUp() {
        Validator validator = ValidatorFactory.getValidator();
        modelValidator = new ModelValidator<>(validator);
    }

    @Test
    void shouldValidateSuccessfullyWithErrors() {
        AccountRequestModel model = requestModelFixture(false);
        model.setBalance(new BigDecimal("-1"));
        try {
            modelValidator.validate(model);
            fail();
        } catch (ViolationException e) {
            assertTrue(e.getViolations().get("firstname").contains("Firstname is required"));
            assertTrue(e.getViolations().get("lastname").contains("Lastname is required"));
            assertTrue(e.getViolations().get("balance").contains("Minimum balance is 0"));
        }
    }

    @Test
    void shouldValidateSuccessfullyWithoutErrors() throws ViolationException {
        AccountRequestModel model = requestModelFixture(true);
        modelValidator.validate(model);
    }
}