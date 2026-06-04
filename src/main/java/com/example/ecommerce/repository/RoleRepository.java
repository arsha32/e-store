package com.example.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.model.AppRole;
import com.example.ecommerce.model.Role;

public interface RoleRepository extends JpaRepository<Role,Long>{

    Optional<Role> findByRoleName(AppRole roleUser);

    
}  