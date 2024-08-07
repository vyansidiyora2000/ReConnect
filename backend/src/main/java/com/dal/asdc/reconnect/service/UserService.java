package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.model.Users;
import com.dal.asdc.reconnect.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersRepository usersRepository;

    /**
     * This method is used to get all the users
     *
     * @param typeId
     * @return List of users
     */
    public List<Users> getUsersByTypeId(int typeId) {
        return usersRepository.findAllUsersByUserTypeTypeID(typeId);
    }

    /**
     * This method is used to delete the user
     *
     * @param userId
     */
    public void deleteUser(int userId) {
        usersRepository.deleteById(userId);
    }
}