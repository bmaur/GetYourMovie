package com.finalproject.filmweb.mail;

import com.finalproject.filmweb.user.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class MailServiceTest {

    private MailService sut;
    @Mock
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() {
        initMocks(this);
        sut = new MailService(javaMailSender);
    }

    @Captor
    ArgumentCaptor<SimpleMailMessage> simpleMailMessageArgumentCaptor;

    @Test
    void shouldSendForgotPasswordEmailCorrectly() {
        //given
        String userEmail = "user@gg.com";
        String forgotPassword = "password";
        String subject = "Forgot password";

        //when
        sut.sendForgotPasswordEmail(userEmail, forgotPassword);
        verify(javaMailSender, times(1)).send(simpleMailMessageArgumentCaptor.capture());
        SimpleMailMessage result = simpleMailMessageArgumentCaptor.getValue();

        //then
        assertThat(result.getSubject()).isEqualTo(subject);
        assertThat(result.getText()).isEqualTo("Its your new password " + forgotPassword);
    }

    @Captor
    ArgumentCaptor<SimpleMailMessage> simpleMailMessageArgumentCaptorForRegisterMail;

    @Test
    void shouldSendRegisterEmailCorrectly() {
        //given
        String url = "randomUrl";
        String userEmail = "user@gg.com";
        String confirmationToken = "randomConfirmationToken";
        String subject = "Registration Confirmation";

        //when
        sut.sendRegisterEmail(userEmail, url, confirmationToken);
        verify(javaMailSender, times(1)).send(simpleMailMessageArgumentCaptorForRegisterMail.capture());
        SimpleMailMessage result = simpleMailMessageArgumentCaptorForRegisterMail.getValue();

        //then
        assertThat(result.getSubject()).isEqualTo(subject);
        assertThat(result.getText()).isEqualTo("To confirm your e-mail address, please click the link below:\n"
                + url + "/firstTimeLogin?token=" + confirmationToken);

    }

    @Captor
    ArgumentCaptor<SimpleMailMessage> simpleMailMessageArgumentCaptorForResetPasswordInformation;

    @Test
    void shouldSendResetPasswordInformationCorrectly() {
        //given
        String userEmail = "user@gg.com";
        String subject = "Password reset";

        //when
        sut.sendResetPasswordInformation(userEmail);
        verify(javaMailSender, times(1)).send(simpleMailMessageArgumentCaptorForResetPasswordInformation.capture());
        SimpleMailMessage result = simpleMailMessageArgumentCaptorForResetPasswordInformation.getValue();

        //then
        assertThat(result.getSubject()).isEqualTo(subject);
        assertThat(result.getText()).isEqualTo("Your GetYourMovie password has been changed.");
    }

    @Captor
    ArgumentCaptor<SimpleMailMessage> simpleMailMessageArgumentCaptorForResetNickInformation;

    @Test
    void shouldSendResetNickInformationCorrectly() {
        //given
        String userEmail = "user@gg.com";
        String userNick = "randomUserNick";
        String subject = "User nick reset";

        //when
        sut.sendResetNickInformation(userEmail,userNick);
        verify(javaMailSender, times(1)).send(simpleMailMessageArgumentCaptorForResetNickInformation.capture());
        SimpleMailMessage result = simpleMailMessageArgumentCaptorForResetNickInformation.getValue();

        //then
        assertThat(result.getSubject()).isEqualTo(subject);
        assertThat(result.getText()).isEqualTo("Your GetYourMovie user nick has been changed for: " + userNick + ".");
    }


}
