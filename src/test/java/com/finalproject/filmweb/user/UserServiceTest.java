package com.finalproject.filmweb.user;

import com.finalproject.filmweb.config.UserSecurityDetailsService;
import com.finalproject.filmweb.user.exception.PasswordNotConfirmedException;
import com.finalproject.filmweb.user.exception.UserNickAlreadyExistException;
import com.finalproject.filmweb.user.exception.UserNotFoundException;
import com.finalproject.filmweb.user.exception.WrongUserPasswordException;
import com.finalproject.filmweb.user.model.UserInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class UserServiceTest {

    private UserService sut;
    @Mock
    private MailService mailService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserSecurityDetailsService userSecurityDetailsService;

    @Captor
    private ArgumentCaptor<UserEntity> userArgumentCaptor;

    @BeforeEach
    void setUp() {
        initMocks(this);
        sut = new UserService(mailService, userRepository, passwordEncoder, userSecurityDetailsService);
    }

    @Test
    void shouldAddNewUserCorrectly() {
        //given
        UserInput userInput = new UserInput();
        userInput.setUserName("testUser");
        userInput.setUserNick("testNick");
        userInput.setUserPassword("password");
        userInput.setUserPasswordConfirm("password");
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(any())).thenReturn(encodedPassword);
        //when
        sut.addNewUser(userInput);
        //then
        verify(passwordEncoder).encode(userInput.getUserPassword());
        verify(userRepository, times(1)).save(userArgumentCaptor.capture());

        UserEntity capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser.getUsername()).isEqualTo(userInput.getUserName());
        assertThat(capturedUser.getUserNick()).isEqualTo(userInput.getUserNick());
        assertThat(capturedUser.getPassword()).isEqualTo(encodedPassword);

    }

    @Test
    void shouldThrowsPasswordNotConfirmedExceptionWhenUserPasswordIsNotEqualToConfirmPassword() {
        //given
        UserInput userInput = new UserInput();
        userInput.setUserName("testUser");
        userInput.setUserNick("testNick");
        userInput.setUserPassword("password");
        userInput.setUserPasswordConfirm("password1");
        final String encodedPassword = "encodedPassword";
        final String errorMessage = "Your password and confirmation password do not match.";
        when(passwordEncoder.encode(any())).thenReturn(encodedPassword);
        //when
        final Throwable result = Assertions.assertThrows(PasswordNotConfirmedException.class,
                () -> sut.addNewUser(userInput));

        verify(passwordEncoder).encode(userInput.getUserPassword());
        verify(userRepository, times(0)).save(any());
        //then
        assertThat(result.getMessage().equals(errorMessage));
    }

    @Test
    void shouldThrowsUserNotFoundExceptionWhenUserNameAreNotTheSame() {
        //given
        UserInput userInput = new UserInput();
        userInput.setUserName("testUser");
        userInput.setUserNick("testNick");
        userInput.setUserPassword("password");
        userInput.setUserPasswordConfirm("password1");
        UserEntity userEntityOptional = new UserEntity();
        final String errorMessage = "Invalid username!";
        userEntityOptional.setUserName("RandomUserName");
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(userEntityOptional));
        //when
        final Throwable result = Assertions.assertThrows(UserNotFoundException.class,
                () -> sut.updateUserPassword(userInput));
        verify(userRepository, times(1)).findByUserName(any());
        //then
        assertThat(result.getMessage().equals(errorMessage));
    }

    @Test
    void shouldUpdateUserPasswordCorrectly() {
        //given
        UserInput userInput = new UserInput();
        userInput.setUserName("testUser");
        userInput.setUserPassword("password");
        userInput.setUserPasswordConfirm("password");
        UserEntity userEntityOptional = new UserEntity();
        userEntityOptional.setPassword("password");
        userEntityOptional.setUserName("testUser");
        when(userRepository.findByUserName(userInput.getUserName())).thenReturn(Optional.of(userEntityOptional));
        when(passwordEncoder.matches(userInput.getUserPassword(), userEntityOptional.getPassword())).thenReturn(true);
        //when
        sut.updateUserPassword(userInput);
        verify(passwordEncoder).encode(userInput.getUserPasswordConfirm());
        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        verify(mailService,times(1)).sendResetPasswordInformation(any());
        //then
        assertThat(userInput.getUserPasswordConfirm()).isEqualTo("password");
    }

    @Test
    void shouldThrowsWrongUserPasswordExceptionWhenUserPasswordsAreNotTheSame() {
        //given
        UserInput userInput = new UserInput();
        userInput.setUserName("testUser");
        userInput.setUserPassword("password");
        userInput.setUserPasswordConfirm("password");
        UserEntity userEntityOptional = new UserEntity();
        userEntityOptional.setPassword("password1");
        userEntityOptional.setUserName("testUser");
        final String errorMessage = "Invalid user password!";
        when(userRepository.findByUserName(userInput.getUserName())).thenReturn(Optional.of(userEntityOptional));
        //when
        final Throwable result = Assertions.assertThrows(WrongUserPasswordException.class,
                () -> sut.updateUserPassword(userInput));
        verify(userRepository, times(0)).save(any());
        verify(mailService, times(0)).sendResetPasswordInformation(any());
        //then
        assertThat(result.getMessage().equals(errorMessage));

    }

    @Test
    void shouldThrowsUserAlreadyExistExceptionWhenUserNickIsNoTheSame() {
        //given
        UserInput userInput = new UserInput();
        userInput.setUserName("testUser");
        userInput.setUserNick("randomUserNick");
        userInput.setUserPassword("password");
        userInput.setUserPasswordConfirm("password");
        UserEntity userEntityOptional = new UserEntity();
        final String errorMessage = "There is an account with that nick name : " + userInput.getUserNick();
        when(userRepository.findByUserNick(userInput.getUserNick())).thenReturn(Optional.of(userEntityOptional));
        //when
        final Throwable result = Assertions.assertThrows(UserNickAlreadyExistException.class,
                () -> sut.updateUserNick(userInput));
        verify(userRepository, times(0)).findByUserName(any());
        verify(userRepository, times(0)).save(any());
        verify(mailService, times(0)).sendResetNickInformation(any(), anyString());
        //then
        assertThat(result.getMessage().equals(errorMessage));

    }

    @Test
    void shouldUpdateUserNickCorrectly() {
        UserInput userInput = new UserInput();
        userInput.setUserName("testUser");
        userInput.setUserNick("randomUserNick");
        userInput.setUserPasswordConfirm("password");
        UserEntity userEntityOptional = new UserEntity();
        userEntityOptional.setUserName("testUser");
        userEntityOptional.setUserNick("randomUserNick");
        when(userRepository.findByUserName(userInput.getUserName())).thenReturn(Optional.of(userEntityOptional));
        //when
        sut.updateUserNick(userInput);
        verify(userRepository, times(1)).save(userEntityOptional);
        verify(mailService, times(1)).sendResetNickInformation(userEntityOptional.getUsername(), userInput.getUserNick());
        //then
        assertThat(userInput.getUserNick()).isEqualTo(userEntityOptional.getUserNick());
    }

    @Captor
    ArgumentCaptor<CharSequence> passwordCaptor;
    @Test
    void shouldChangeUserPasswordCorrectly() {
        //given
        UserInput userInput = new UserInput();
        userInput.setUserName("testUser");
        UserEntity userEntityOptional = new UserEntity();
        userEntityOptional.setUserName("testUser");
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(userEntityOptional));

        //when
        sut.newPassword(userInput.getUserName());
        verify(passwordEncoder, times(1)).encode(passwordCaptor.capture());
        CharSequence capturedPassword = passwordCaptor.getValue();
        verify(userRepository, times(1)).findByUserName(userInput.getUserName());
        verify(userRepository, times(1)).save(any());
        verify(mailService).sendForgotPasswordEmail(userInput.getUserName(), capturedPassword.toString());

        //then
        assertThat(capturedPassword).hasSize(12);

    }

}

