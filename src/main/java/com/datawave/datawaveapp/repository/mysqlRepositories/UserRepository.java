package com.datawave.datawaveapp.repository.mysqlRepositories;

import com.datawave.datawaveapp.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
