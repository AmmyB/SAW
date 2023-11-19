package com.project.saw.user;

import com.project.saw.dto.user.UserProjections;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    Optional<UserEntity> findByUserNameIgnoreCase(String userName);

    @Query("SELECT u FROM UserEntity u")
    List<UserProjections> listOfUsers (Sort sort);
}
