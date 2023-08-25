package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendShipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("UserDb")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendsDbStorage friendsDbStorage;

    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendsDbStorage friendsDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendsDbStorage = friendsDbStorage;
    }

    @Override
    public User add(User item) {
        SimpleJdbcInsert  simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("PUBLIC.USERS")
                .usingColumns("EMAIL", "LOGIN", "USER_NAME", "BIRTHDAY")
                .usingGeneratedKeyColumns("USER_ID");
        item.setId(simpleJdbcInsert.executeAndReturnKey(item.toMap()).longValue());
        return item;
    }

    @Override
    public void delete(Long id) {
        if (contains(id)) {
            jdbcTemplate.update("DELETE FROM PUBLIC.USERS WHERE USER_ID = ?", id);
        }
    }

    @Override
    public User update(User item) {
        String sqlQuery = "UPDATE PUBLIC.USERS SET " +
                "EMAIL = ?, LOGIN = ?, USER_NAME = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        if (contains(item.getId())) {
            jdbcTemplate.update(sqlQuery, item.getEmail(), item.getLogin(), item.getName(), item.getBirthday(), item.getId());
        } else {
            throw new NotFoundException("item with id doesn't exist - " + item.getId());
        }
        friendsDbStorage.saveFriendship(item);
        return item;
    }

    @Override
    public boolean contains(Long id) {
        String sqlQuery = "SELECT USER_ID FROM PUBLIC.USERS WHERE USER_ID = ?";
        return jdbcTemplate.queryForRowSet(sqlQuery, id).next();
    }

    @Override
    public User getItem(Long id) {
        String sqlQuery = "SELECT USER_ID, EMAIL, LOGIN, USER_NAME, BIRTHDAY" +
                " FROM PUBLIC.USERS WHERE USER_ID = ?";
        User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        user.setFriends(friendsDbStorage.getFriendList(id));
        return user;
    }

    @Override
    public List<User> getAllItemsList() {
        List<User> users = jdbcTemplate.query("SELECT * FROM PUBLIC.USERS",this::makeUser);
        var friends = friendsDbStorage.getFriends();
        for (User user : users) {
            if (friends.containsKey(user.getId())) {
                user.setFriends(friends.get(user.getId()));
            }
        }
        return users;
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("USER_ID"));
        user.setEmail(rs.getString("EMAIL"));
        user.setLogin(rs.getString("LOGIN"));
        user.setName(rs.getString("USER_NAME"));
        user.setBirthday(rs.getDate("BIRTHDAY"));
        return user;
    }

    private Map<Long, FriendShipStatus> mapRowToMapOfFriendship (ResultSet rs, int rn) throws SQLException {
        Map map = new HashMap<Long, FriendShipStatus>();
        map.put(rs.getLong("OTHER_USER_ID"),FriendShipStatus.valueOf(rs.getString("STATUS")));
        return map;
    }

    private User mapRowToUser(ResultSet rs, int rn) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("USER_ID"));
        user.setEmail(rs.getString("EMAIL"));
        user.setLogin(rs.getString("LOGIN"));
        user.setName(rs.getString("USER_NAME"));
        user.setBirthday(rs.getDate("BIRTHDAY"));

        return user;
    }
}
