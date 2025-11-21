package com.usuarios.Demo.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity //Para indicar que esta sera la tabla
@Table(name= "admins")
@AllArgsConstructor //Constructor llenito
@NoArgsConstructor //Constructor vacio
@Data //Nos trae todos los getters, setters
public class AdminModel {
    @Id
    @GeneratedValue
    private UUID id; //Universal Unique ID
    private String username;
    private String lastname;
    private String rut;
    private String avatarURL;
    private String address;
    private String email;
    private String password;
    private String rol;

}
