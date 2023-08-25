package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PastOrPresentValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MyPastOrPresent {
        String message() default "{дата должна быть не из будущего}";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
}
