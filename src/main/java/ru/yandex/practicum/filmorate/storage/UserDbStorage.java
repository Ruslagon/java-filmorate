package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendShipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("UserDb")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
        saveFriendship(item);
        return item;
    }

    private void saveFriendship(User item) {

        final Long userId = item.getId();
        jdbcTemplate.update("DELETE FROM PUBLIC.FRIENDS WHERE USER_ID = ?", userId);
        Map<Long, FriendShipStatus> friends = item.getFriends();
        if (friends == null || friends.isEmpty()) {
            return;
        }
        List<Map.Entry<Long, FriendShipStatus>> friendshipList = new ArrayList<>(friends.entrySet());
        //final ArrayList<Genre> genreList = new ArrayList<>(friends);
        jdbcTemplate.batchUpdate(
                "insert into PUBLIC.FRIENDS (USER_ID, OTHER_USER_ID, STATUS_ID) values (?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, userId);
                        ps.setLong(2, friendshipList.get(i).getKey());
                        ps.setInt(3,indexOfFriendshipStatus(friendshipList.get(i).getValue()));
                    }

                    public int getBatchSize() {
                        return friendshipList.size();
                    }
                });

//        Map<Long,FriendShipStatus> newFriendList = item.getFriends();
//        Map<Long, FriendShipStatus> oldFriendList = getFriendList(item.getId());
//        List<Map.Entry<Long, FriendShipStatus>> newFriends = newFriendList.entrySet().stream()
//                .filter(entry -> !oldFriendList.containsKey(entry.getKey())).collect(Collectors.toList());
//        List<Map.Entry<Long, FriendShipStatus>> oldLostFriends = oldFriendList.entrySet().stream()
//                .filter(entry -> !newFriendList.containsKey(entry.getKey())).collect(Collectors.toList());
//        List<Map.Entry<Long, FriendShipStatus>> alteredFriendship = oldFriendList.entrySet().stream()
//                .filter(entry -> newFriendList.containsKey(entry.getKey()))
//                .filter(entry -> !newFriendList.get(entry.getKey()).equals(entry.getValue())).collect(Collectors.toList());
//        for (Map.Entry<Long, FriendShipStatus> newFriend : newFriends) {
//            int statusId;
//            SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT STATUS_ID FROM PUBLIC.FRIENDS_STATUS WHERE STATUS = ?"
//                    , newFriend.getValue().name());
//            if (rowSet.next()) {
//                statusId = rowSet.getInt("STATUS_ID");
//            } else {
//                throw new RuntimeException("неизвестная ошибка");
//            }
//            jdbcTemplate.update("INSERT INTO PUBLIC.FRIENDS (USER_ID, OTHER_USER_ID, STATUS_ID) " +
//                    "VALUES (?,?,?);", item.getId(),newFriend.getKey(),statusId);
//        }
//        for (Map.Entry<Long, FriendShipStatus> lostFriend : oldLostFriends) {
//            jdbcTemplate.update("DELETE FROM PUBLIC.FRIENDS WHERE USER_ID = ? AND OTHER_USER_ID = ?", item.getId(),lostFriend.getKey());
//        }
//        for (Map.Entry<Long, FriendShipStatus> alteredFriend : alteredFriendship) {
//            int statusId = jdbcTemplate.queryForRowSet("SELECT STATUS_ID FROM PUBLIC.FRIENDS_STATUS WHERE STATUS = ?"
//                    , alteredFriend.getValue().name()).getInt("STATUS_ID");
//            jdbcTemplate.update("UPDATE PUBLIC.FRIENDS SET STATUS_ID = ? WHERE USER_ID = ? AND OTHER_USER_ID = ?"
//                    ,statusId, item.getId(),alteredFriend.getKey());
//        }
    }

    private int indexOfFriendshipStatus(FriendShipStatus status) {
        if (status.equals(status.UNCONFIRMED)) {
            return 1;
        } else {
            return 2;
        }
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
        user.setFriends(getFriendList(id));
        return user;
    }

    private HashMap<Long, FriendShipStatus> getFriendList(Long id) {
        HashMap<Long, FriendShipStatus> friends = new HashMap<>();
        String sqlQueryForFriendList = "SELECT f.OTHER_USER_ID, fs.STATUS" +
                " FROM PUBLIC.FRIENDS AS f JOIN PUBLIC.FRIENDS_STATUS AS fs ON f.STATUS_ID = fs.STATUS_ID  WHERE USER_ID = ?";
        SqlRowSet friendStatusRows = jdbcTemplate.queryForRowSet(sqlQueryForFriendList,id);
        while (friendStatusRows.next()) {
            friends.put(friendStatusRows.getLong("OTHER_USER_ID"),FriendShipStatus.valueOf(friendStatusRows.getString("STATUS")));
        }
        return friends;
    }

    @Override
    public List<User> getAllItemsList() {
        List<User> users = jdbcTemplate.query("SELECT * FROM PUBLIC.USERS",this::makeUser);
        var friends = getFriends();
        for (User user : users) {
            if (friends.containsKey(user.getId())) {
                user.setFriends(friends.get(user.getId()));
            }
        }
//        String sqlQuery = "SELECT USER_ID FROM PUBLIC.USERS";
//        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery);
//        while (userRows.next()) {
//            users.add(getItem(userRows.getLong("USER_ID")));
//        }
//        return users;
        return users;
    }

    private HashMap<Long,HashMap<Long, FriendShipStatus>> getFriends() {
        HashMap<Long, HashMap<Long, FriendShipStatus>> userFriends = new HashMap<>();
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet("SELECT USER_ID, OTHER_USER_ID, STATUS_ID FROM PUBLIC.FRIENDS");
        while (friendsRows.next()) {
            Long userId = friendsRows.getLong("USER_ID");
            Long friendId = friendsRows.getLong("OTHER_USER_ID");
            FriendShipStatus status = FriendShipStatus.valueOf(getStatus(friendsRows.getInt("STATUS_ID")));
            HashMap<Long, FriendShipStatus> emptyFriends = new HashMap<>();
            HashMap<Long, FriendShipStatus> updatedFriends = userFriends.getOrDefault(friendId, emptyFriends);
            updatedFriends.put(friendId, status);
            userFriends.put(userId, updatedFriends);
        }
        return userFriends;
    }

    private String getStatus(int statusId) {
        if (statusId == 1) {
            return FriendShipStatus.UNCONFIRMED.toString();
        } else {
            return FriendShipStatus.CONFIRMED.toString();
        }
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
