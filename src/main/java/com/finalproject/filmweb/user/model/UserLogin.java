package com.finalproject.filmweb.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;
@Getter
@Setter
@RequiredArgsConstructor
public class UserLogin {

    private String userName;

    private String userPassword;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLogin userLogin = (UserLogin) o;
        return Objects.equals(userName, userLogin.userName) &&
                Objects.equals(userPassword, userLogin.userPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, userPassword);
    }
}
