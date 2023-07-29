package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@SpringBootTest
class FilmorateApplicationTests {

	@Test
	void validationFilm() throws ValidationException {
		/*
		FilmController controller = new FilmController();
		LocalDate beforeFirstMovieDate = LocalDate.of(1895,12,27);
		LocalDate firstMovieDate = LocalDate.of(1895,12,28);
		LocalDate afterFirstMovieDate = LocalDate.of(1895,12,29);
		String smallDescription = "description";
		String symbolsOf200Description = "��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 ����?";
		String tooBigDescription = "��� �� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 �������� ��� 200 ����?";
		int minusDuration = -100;
		int zeroDuration = 0;
		int plusDuration = 120;
		String name = "name";
		String blankName = "";
		assertFalse(controller.validation(new Film(name,smallDescription, afterFirstMovieDate, plusDuration)));
		assertFalse(controller.validation(new Film(name,smallDescription, firstMovieDate, plusDuration)));
		assertFalse(controller.validation(new Film(name,symbolsOf200Description, afterFirstMovieDate, plusDuration)));

		assertTrue(controller.validation(new Film(name,smallDescription, beforeFirstMovieDate, plusDuration)));
		assertTrue(controller.validation(new Film(name,tooBigDescription, afterFirstMovieDate, plusDuration)));
		assertTrue(controller.validation(new Film(name,smallDescription, afterFirstMovieDate, minusDuration)));
		assertTrue(controller.validation(new Film(name,smallDescription, afterFirstMovieDate, zeroDuration)));
		assertTrue(controller.validation(new Film(blankName, smallDescription, afterFirstMovieDate, plusDuration)));
		Film filmChecker = new Film();
		Exception ex = new Exception("hi");
		assertThrows(Exception.class, () -> {filmChecker.setDuration(minusDuration);});
		 */
	}

	@Test
	void validationUser() throws ValidationException {
		/*
		LocalDate past = LocalDate.of(1895,12,27);
		LocalDate future = LocalDate.of(2077,12,27);
		String blankLogin = "";
		String login = "persona";
		String blankEmail = "";
		String wrongEmail = "email";
		String email = "jojo@mail.ru";
		UserController controller = new UserController();
		assertFalse(controller.validation(new User(email,login,past)));
		assertTrue(controller.validation(new User(blankEmail,login,past)));
		assertTrue(controller.validation(new User(wrongEmail,login,past)));
		assertTrue(controller.validation(new User(email,blankLogin,past)));
		assertTrue(controller.validation(new User(email,login,future)));
		 */
	}

	@Test
	void contextLoads() {
	}

}
