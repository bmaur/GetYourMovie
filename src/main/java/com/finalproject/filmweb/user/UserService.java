package com.finalproject.filmweb.user;

import com.finalproject.filmweb.config.UserSecurityDetailsService;
import com.finalproject.filmweb.user.exception.*;
import com.finalproject.filmweb.user.model.UserInput;
import com.finalproject.filmweb.user.model.UserLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final MailService mailService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserSecurityDetailsService userSecurityDetailsService;

    public void addNewUser(UserInput userInput) throws UserAlreadyExistException {
        validateEmail(userInput.getUserName());
        validateUserNick(userInput.getUserNick());

        UserEntity user = new UserEntity();
        user.setUserName(userInput.getUserName());
        user.setUserNick(userInput.getUserNick());
        user.setPassword(passwordEncoder.encode(userInput.getUserPassword()));

        if (userInput.getUserPassword().equals(userInput.getUserPasswordConfirm())) {
            user.setEnabled(false);
            user.setConfirmationToken(UUID.randomUUID().toString());
            user.setCreatedOn(LocalDateTime.now());
            userRepository.save(user);
        } else
            throw new PasswordNotConfirmedException();
    }

    public void updateUserPassword(UserInput userInput) {
        Optional<UserEntity> userEntity = userRepository.findByUserName(userInput.getUserName());
        if (!(userEntity.get().getUsername().equals(userInput.getUserName()))) {
            throw new UserNotFoundException();
        }
        if (passwordEncoder.matches(userInput.getUserPassword(), userEntity.get().getPassword())) {
            userEntity.get().setPassword(passwordEncoder.encode(userInput.getUserPasswordConfirm()));
            userRepository.save(userEntity.get());
            mailService.sendResetPasswordInformation(userEntity.get().getUsername());
        } else throw new WrongUserPasswordException();

    }

    public void updateUserNick(UserInput userInput) throws UserNickAlreadyExistException {
        String newUserNick = userInput.getUserNick();
        validateUserNick(newUserNick);
        userRepository.findByUserName(userInput.getUserName()).ifPresent(
                u -> {
                    u.setUserNick(newUserNick);
                    userRepository.save(u);
                    mailService.sendResetNickInformation(u.getUsername(), newUserNick);
                }
        );
    }

    public Optional<UserEntity> showUserInformation(String userName) {
        return userRepository.findByUserName(userName);
    }

    public void newPassword(String userEmail) {
        String password = randomPasswordGenerator(12);
        UserEntity userEntity = userRepository.findByUserName(userEmail)
                .orElseThrow(UserNotFoundException::new);

        mailService.sendForgotPasswordEmail(userEmail, password);
        userEntity.setPassword(passwordEncoder.encode(password));
        userRepository.save(userEntity);
    }

    private String randomPasswordGenerator(int len) {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnoprstuvwxyz";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }


    private void validateEmail(final String email) {
        userRepository.findByUserName(email).ifPresent(x -> {
            throw new UserAlreadyExistException(email);
        });
    }

    private void validateUserNick(final String userNick) {
        userRepository.findByUserNick(userNick).ifPresent(x -> {
            throw new UserNickAlreadyExistException(userNick);
        });
    }

    private void validateLoginPassword(String inputPassword, String dataPassword) {
        if (!passwordEncoder.matches(inputPassword, dataPassword))
            throw new WrongUserPasswordException();
    }

    public String getUserNick(String userEmail) {
        UserEntity userEntity = userRepository.findByUserName(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userEntity.getUserNick();

    }

    public void logToService(UserLogin userLogin) {
        userSecurityDetailsService.loadUserByUsername(userLogin.getUserName());
        validateLoginPassword(userLogin.getUserPassword(), userSecurityDetailsService.loadUserByUsername(userLogin.getUserName()).getPassword());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByUserName(email);
    }

    public Optional<UserEntity> findByConfirmationToken(String token) {
        return userRepository.findByConfirmationToken(token);
    }

    public void setTrueForUserEnabledAndSave(UserEntity user) {
        userRepository.findByUserName(user.getUsername()).ifPresent(
                u -> {
                    u.setEnabled(true);
                    userRepository.save(u);
                }
        );
    }
}






