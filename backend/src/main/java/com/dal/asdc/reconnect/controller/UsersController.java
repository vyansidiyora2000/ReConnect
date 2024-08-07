package com.dal.asdc.reconnect.controller;

import com.dal.asdc.reconnect.dto.Users.UserNameTypeIdDTO;
import com.dal.asdc.reconnect.model.Users;
import com.dal.asdc.reconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UserService userService;

    /**
     * Retrieves a list of users by their user type ID.
     *
     * @param typeId The ID of the user type to filter by
     * @return ResponseEntity containing a list of UserNameTypeIdDTO objects if users are found,
     * or an empty list with CONFLICT status if no users are found
     */
    @GetMapping("/getAllUsersByTypeId")
    public ResponseEntity<List<UserNameTypeIdDTO>> getUserNamesAndTypeIdsByUserType(@RequestParam int typeId) {
        List<Users> users = userService.getUsersByTypeId(typeId);
        if (users.size() == 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ArrayList<>());
        }
        List<UserNameTypeIdDTO> listOfUsers = new ArrayList<>();
        for (Users user : users) {
            UserNameTypeIdDTO tempUser = new UserNameTypeIdDTO(user.getUserID(), user.getUsername(), user.getUserType().getTypeID());
            listOfUsers.add(tempUser);
        }
        return ResponseEntity.ok(listOfUsers);
    }

    /**
     * Deletes a user by their user ID.
     *
     * @param userId The ID of the user to delete
     * @return ResponseEntity with NO_CONTENT status if the user is deleted successfully,
     * or INTERNAL_SERVER_ERROR status if an exception occurs
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteUser(@PathVariable int userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
