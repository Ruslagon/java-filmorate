package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FirstFilmDateConstraintValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FirstFilmDate {
    String message() default "{дата фильма должна быть не раньше 28 декабря 1895 года}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
