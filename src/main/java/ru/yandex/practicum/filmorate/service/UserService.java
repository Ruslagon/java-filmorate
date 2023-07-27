package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    @Qualifier("inMemoryUser")
    UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.getAllItemsList();
    }

    public User create(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
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
        userStorage.getItem(user1Id).addFriends(user2Id);
        userStorage.getItem(user2Id).addFriends(user1Id);
        friendLists.put(user1Id,userStorage.getItem(user1Id).getFriendsIds());
        friendLists.put(user2Id,userStorage.getItem(user2Id).getFriendsIds());
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
        userStorage.getItem(user1Id).deleteFriends(user2Id);
        userStorage.getItem(user2Id).deleteFriends(user1Id);
        friendLists.put(user1Id,userStorage.getItem(user1Id).getFriendsIds());
        friendLists.put(user2Id,userStorage.getItem(user2Id).getFriendsIds());
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
        } ).filter(user -> friendsIds.contains(user.getId())).collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Long user1Id, Long user2Id) {
        Set<Long> firstUserList = getFriendsIds(user1Id);
        Set<Long> secondUserList = getFriendsIds(user2Id);
        Set<Long> commonFriendsId = firstUserList.stream().filter(secondUserList::contains).collect(Collectors.toSet());
        return userStorage.getAllItemsList().stream().filter(user -> commonFriendsId.contains(user.getId())).collect(Collectors.toList());
    }
}
