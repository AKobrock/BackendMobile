package com.usuarios.Demo.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.usuarios.Demo.model.UserModel;

@Repository
public interface IUserModelRepository extends JpaRepository<UserModel, UUID> {

    UserModel findByEmail(String email);
    
} 
