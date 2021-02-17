package com.finalproject.filmweb.user;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    public void sendForgotPasswordEmail(String userEmail, String forgotPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Forgot password");
        message.setText("Its your new password " + forgotPassword);
        javaMailSender.send(message);
    }

    public void sendRegisterEmail(String userEmail, String url, String confirmationToken) {
        SimpleMailMessage registrationEmail = new SimpleMailMessage();
        registrationEmail.setTo(userEmail);
        registrationEmail.setSubject("Registration Confirmation");
        registrationEmail.setText("To confirm your e-mail address, please click the link below:\n"
                + url + "/firstTimeLogin?token=" + confirmationToken);
        javaMailSender.send(registrationEmail);
    }

    public void sendResetPasswordInformation(String userEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Password reset");
        message.setText("Your GetYourMovie password has been changed.");
        javaMailSender.send(message);
    }

    public void sendResetNickInformation(String userEmail, String userNick) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("User nick reset");
        message.setText("Your GetYourMovie user nick has been changed for: " + userNick + ".");
        javaMailSender.send(message);
    }
}
