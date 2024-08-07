package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.model.UserType;
import com.dal.asdc.reconnect.model.Users;
import com.dal.asdc.reconnect.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUsersByTypeId() {
        UserType userType = new UserType();
        userType.setTypeID(1);

        Users user = new Users();
        user.setUserID(1);
        user.setUserType(userType);

        List<Users> usersList = new ArrayList<>();
        usersList.add(user);

        when(usersRepository.findAllUsersByUserTypeTypeID(1)).thenReturn(usersList);

        List<Users> result = userService.getUsersByTypeId(1);

        assertEquals(usersList, result);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(usersRepository).deleteById(1);

        userService.deleteUser(1);

        verify(usersRepository, times(1)).deleteById(1);
    }
}
