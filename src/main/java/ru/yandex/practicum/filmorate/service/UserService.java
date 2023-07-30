package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Autowired
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

    public Map<Long, Set<Long>> addFriend(Long user1Id, Long user2Id) {
        Map<Long,Set<Long>> friendLists = new HashMap<>();
        if (!userStorage.contains(user1Id)) {
            throw new NotFoundException("this id doesn't exist - " + user1Id);
        }
        if (!userStorage.contains(user2Id)) {
            throw new NotFoundException("this id doesn't exist - " + user2Id);
        }

        User user1 = userStorage.getItem(user1Id);
        User user2 = userStorage.getItem(user2Id);

        user1.addFriends(user2Id);
        user2.addFriends(user1Id);

        friendLists.put(user1Id, user1.getFriendsIds());
        friendLists.put(user2Id, user2.getFriendsIds());
        return friendLists;
    }

    public Map<Long,Set<Long>> deleteFromFriends(Long user1Id, Long user2Id) {
        Map<Long,Set<Long>> friendLists = new HashMap<>();
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

        friendLists.put(user1Id, user1.getFriendsIds());
        friendLists.put(user2Id, user2.getFriendsIds());
        return friendLists;
    }

    public Set<Long> getFriendsIds(Long id) {
        if (!userStorage.contains(id)) {
            throw new NotFoundException("this id doesn't exist - " + id);
        }
        Set<Long> friendsIds = userStorage.getItem(id).getFriendsIds();
        if (friendsIds == null) {
            friendsIds = new HashSet<>();
        }
        return friendsIds;
    }

    public List<User> getFriends(Long id) {
        Set<Long> friendsIds = getFriendsIds(id);
        return userStorage.getAllItemsList().stream().sorted((user1,user2) -> {
            int comp = user1.getId().compareTo(user2.getId());
        return comp;
        }).filter(user -> friendsIds.contains(user.getId())).collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Long user1Id, Long user2Id) {
        Set<Long> firstUserList = getFriendsIds(user1Id);
        Set<Long> secondUserList = getFriendsIds(user2Id);
        Set<Long> commonFriendsId = firstUserList.stream().filter(secondUserList::contains).collect(Collectors.toSet());
        return userStorage.getAllItemsList().stream().filter(user -> commonFriendsId.contains(user.getId())).collect(Collectors.toList());
    }

    private void nameFix(User user){
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
