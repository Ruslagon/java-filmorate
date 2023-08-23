package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Date;
import java.time.LocalDate;

public class FirstFilmDateConstraintValidator implements ConstraintValidator<FirstFilmDate, Date> {
    private static final LocalDate earliestFilmDate = LocalDate.of(1895,12,28);

    @Override
    public void initialize(FirstFilmDate releaseDate) {
        //
    }

    @Override
    public boolean isValid(Date filmReleaseDate, ConstraintValidatorContext cxt) {
        if (filmReleaseDate == null) {
            return false;
        }
        return !filmReleaseDate.before(java.sql.Date.valueOf(earliestFilmDate));
    }

}
