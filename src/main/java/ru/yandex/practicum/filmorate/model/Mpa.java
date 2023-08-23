package ru.yandex.practicum.filmorate.model;

public class Mpa {

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        switch (id) {
            case (1):
                name = "G";
                break;
            case (2):
                name = "PG";
                break;
            case (3):
                name = "PG-13";
                break;
            case (4):
                name = "R";
                break;
            default:
                name = "NC-17";
                break;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
