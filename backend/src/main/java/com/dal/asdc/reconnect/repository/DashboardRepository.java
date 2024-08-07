package com.dal.asdc.reconnect.repository;

import com.dal.asdc.reconnect.dto.Dashboard.UsersPerCompanyDTO;
import com.dal.asdc.reconnect.dto.Dashboard.UsersPerCountryDTO;
import com.dal.asdc.reconnect.dto.Dashboard.UsersPerTypeDTO;
import com.dal.asdc.reconnect.model.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Users, Integer> {
    @Query("select new com.dal.asdc.reconnect.dto.Dashboard.UsersPerCountryDTO(count(u.userID), c.countryName, c.countryId) from Users u join u.userDetails ud right join ud.country c group by c.countryId, c.countryName")
    List<UsersPerCountryDTO> getAllUsersPerCountry();

    @Query("select new com.dal.asdc.reconnect.dto.Dashboard.UsersPerTypeDTO(count(u.userID), ut.typeID, ut.typeName) from Users u right join u.userType ut group by ut.typeID, ut.typeName")
    List<UsersPerTypeDTO> getAllUsersPerType();

    @Query("select new com.dal.asdc.reconnect.dto.Dashboard.UsersPerCompanyDTO(count(u.userID), c.companyName, c.companyId) from Users u join u.userDetails ud right join ud.company c group by c.companyId, c.companyName")
    List<UsersPerCompanyDTO> getAllUsersPerCompany(Pageable pageable);
}
