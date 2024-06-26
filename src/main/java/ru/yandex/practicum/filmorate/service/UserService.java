package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendShipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Autowired
    @Qualifier("UserDb")
    private UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.getAllItemsList();
    }

    public User create(User user) {
        nameFix(user);
        log.info("добавлен пользователь : {}", user.toString());
        return userStorage.add(user);
    }

    public User update(User user) {
        nameFix(user);
        return userStorage.update(user);
    }

    public User getUserById(Long id) {
        if (!userStorage.contains(id)) {
            throw new NotFoundException("данный пользователь не найден -" + id);
        }
        return userStorage.getItem(id);
    }

    public Map<Long, Map<Long, FriendShipStatus>> addFriend(Long user1Id, Long user2Id) {
        Map<Long, Map<Long, FriendShipStatus>> friendLists = new HashMap<>();
        if (!userStorage.contains(user1Id)) {
            throw new NotFoundException("this id doesn't exist - " + user1Id);
        }
        if (!userStorage.contains(user2Id)) {
            throw new NotFoundException("this id doesn't exist - " + user2Id);
        }

        User user1 = userStorage.getItem(user1Id);
        User user2 = userStorage.getItem(user2Id);

        FriendShipStatus user2Status = user2.getFriendshipStatus(user1Id);
        if (user2Status == null) {
            user1.addFriends(user2Id, FriendShipStatus.UNCONFIRMED);
        }
        if (user2Status == FriendShipStatus.UNCONFIRMED) {
            user1.addFriends(user2Id, FriendShipStatus.CONFIRMED);
            user2.addFriends(user1Id, FriendShipStatus.CONFIRMED);
        }
        if (user2Status == FriendShipStatus.CONFIRMED) {
            user1.addFriends(user2Id, FriendShipStatus.CONFIRMED);
        }

        userStorage.update(user1);
        userStorage.update(user2);

        friendLists.put(user1Id, user1.getFriends());
        friendLists.put(user2Id, user2.getFriends());

//        friendLists.put(user1Id, userStorage.getItem(user1Id).getFriends());
//        friendLists.put(user2Id, userStorage.getItem(user2Id).getFriends());
        return friendLists;
    }

    public Map<Long,Map<Long, FriendShipStatus>> deleteFromFriends(Long user1Id, Long user2Id) {
        Map<Long,Map<Long, FriendShipStatus>> friendLists = new HashMap<>();
        if (!userStorage.contains(user1Id)) {
            throw new NotFoundException("this id doesn't exist - " + user1Id);
        }
        if (!userStorage.contains(user2Id)) {
            throw new NotFoundException("this id doesn't exist - " + user2Id);
        }
        User user1 = userStorage.getItem(user1Id);
        User user2 = userStorage.getItem(user2Id);

        user1.deleteFriends(user2Id);
        user2.deleteFriends(user1Id);

        userStorage.update(user1);
        userStorage.update(user2);

        friendLists.put(user1Id, user1.getFriends());
        friendLists.put(user2Id, user2.getFriends());
        return friendLists;
    }

    public List<Long> getFriendsIds(Long id) {
        if (!userStorage.contains(id)) {
            throw new NotFoundException("this id doesn't exist - " + id);
        }
        List<Long> friendsIds = new ArrayList<>(userStorage.getItem(id).getFriends().keySet());
        return friendsIds;
    }

    public List<User> getFriends(Long id) {
        List<Long>  friendsIds = getFriendsIds(id);
        return userStorage.getAllItemsList().stream().sorted((user1,user2) -> {
            int comp = user1.getId().compareTo(user2.getId());
        return comp;
        }).filter(user -> friendsIds.contains(user.getId())).collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Long user1Id, Long user2Id) {
        List<Long> firstUserList = getFriendsIds(user1Id);
        List<Long> secondUserList = getFriendsIds(user2Id);
        Set<Long> commonFriendsId = firstUserList.stream().filter(secondUserList::contains).collect(Collectors.toSet());
        return userStorage.getAllItemsList().stream().filter(user -> commonFriendsId.contains(user.getId())).collect(Collectors.toList());
    }

    private void nameFix(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
