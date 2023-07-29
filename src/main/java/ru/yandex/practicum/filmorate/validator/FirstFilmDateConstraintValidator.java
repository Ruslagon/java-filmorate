package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FirstFilmDateConstraintValidator implements ConstraintValidator<FirstFilmDate, LocalDate> {
    private final LocalDate earliestFilmDate = LocalDate.of(1895,12,28);

    @Override
    public void initialize(FirstFilmDate releaseDate) {
        //
    }

    @Override
    public boolean isValid(LocalDate filmReleaseDate, ConstraintValidatorContext cxt) {
        if (filmReleaseDate == null) {
            return false;
        }
        return !filmReleaseDate.isBefore(earliestFilmDate);
    }

}
