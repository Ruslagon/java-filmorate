package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Date;
import java.time.Instant;

public class PastOrPresentValidator implements ConstraintValidator<MyPastOrPresent, Date> {
    @Override
    public void initialize(MyPastOrPresent date) {
        //
    }

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext cxt) {
        if (date == null) {
            return false;
        }
        return date.before(java.util.Date.from(Instant.now()));
    }

}

