package com.project.saw.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserNameIgnoreCase(String userName);
}
