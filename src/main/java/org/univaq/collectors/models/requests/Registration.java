package org.univaq.collectors.models.requests;

import java.sql.Date;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class Registration {

    private String name;

    private String surname;

    @NotBlank
    private String username;
    
    private Date date;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Password is required")
    private String password;


    public Registration() {}

    public Registration(String name, String surname, String username, Date date, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.date = date;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
}
