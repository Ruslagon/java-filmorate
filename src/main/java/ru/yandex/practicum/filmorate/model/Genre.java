package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Genre {

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        switch (id) {
            case (1):
                name = "Комедия";
                break;
            case (2):
                name = "Драма";
                break;
            case (3):
                name = "Мультфильм";
                break;
            case (4):
                name = "Триллер";
                break;
            case (5):
                name = "Документальный";
                break;
            default:
                name = "Боевик";
                break;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> genresToMapBd (long id) {
        Map<String, Object> values = new HashMap<>();
        values.put("FILM_ID", id);
        values.put("GENRE_ID", id);
        return values;
    }
}
