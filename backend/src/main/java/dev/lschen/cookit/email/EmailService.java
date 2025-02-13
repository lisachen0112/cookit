package dev.lschen.cookit.email;

import dev.lschen.cookit.activation.ActivationToken;
import dev.lschen.cookit.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(
            String to,
            EmailTemplateName emailTemplate,
            String subject,
            Map<String, Object> properties
    ) throws MessagingException {
        String templateName;
        if (emailTemplate == null) {
            templateName = "confirm-email";
        } else {
            templateName = emailTemplate.name();
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MULTIPART_MODE_MIXED,
                UTF_8.name()
        );

        Context context = new Context();
        if (!properties.isEmpty()) {
            context.setVariables(properties);
        }

        helper.setFrom("contact@cookit.com");
        helper.setTo(to);
        helper.setSubject(subject);

        String template = templateEngine.process(templateName, context);
        helper.setText(template, true);
        mailSender.send(mimeMessage);}

    public void sendActivationEmail(User user, String activationCode) throws MessagingException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", user.getUsername());
        properties.put("activationCode", activationCode);

        sendEmail(
            user.getEmail(),
            EmailTemplateName.ACTIVATE_ACCOUNT,
            "Account activation",
            properties
    );
    }
}
