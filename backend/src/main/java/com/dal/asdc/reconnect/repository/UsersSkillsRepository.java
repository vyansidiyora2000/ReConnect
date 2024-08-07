package com.dal.asdc.reconnect.repository;

import com.dal.asdc.reconnect.model.UserSkills;
import com.dal.asdc.reconnect.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsersSkillsRepository extends JpaRepository<UserSkills, Integer>
{
    List<UserSkills> findByUsersUserID(int userID);

    void deleteByUsers(Users user);
}
