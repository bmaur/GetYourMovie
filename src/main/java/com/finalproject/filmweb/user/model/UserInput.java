package com.finalproject.filmweb.user.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
@Getter
@Setter
@RequiredArgsConstructor
public class UserInput implements Serializable {

    @NotNull(message = "Name cannot be empty and must be an email address")
    @Email(message = "Username must by an email address")
    private String userName;

    @NotNull(message = "Nick cannot be empty")
    @Size(min = 5, max = 10, message = "Range of nick must be between 5 - 10 characters")
    private String userNick;

    @NotNull(message = "Password cannot be empty ")
    @Pattern(regexp = "[A-Za-z\\d#$%^&*!@]{8,24}", message = "\"Password must have at least 8 characters \"")
    private String userPassword;


    @Pattern(regexp = "[A-Za-z\\d#$%^&*!@]{8,24}", message = "\"Password must have at least 8 characters \"")
    @NotNull(message = "Password confirmation cannot be empty ")
    private String userPasswordConfirm;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInput userInput = (UserInput) o;
        return Objects.equals(userName, userInput.userName) &&
                Objects.equals(userPassword, userInput.userPassword) &&
                Objects.equals(userNick, userInput.userNick);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, userNick, userPassword);
    }

    @Override
    public String toString() {
        return "UserInput{" +
                "userName='" + userName + '\'' +
                ", userNick='" + userNick + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userPasswordConfirm='" + userPasswordConfirm + '\'' +
                '}';
    }
}
