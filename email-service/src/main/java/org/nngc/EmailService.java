package org.nngc;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class EmailService implements EmailSender {

    @Value("${sendgrid.api-key:}")
    private String sendgridApiKey;




    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);


    @Override
    public void sendWithSendGrid(String to, String subject, Content content) throws IOException {
        Email from = new Email("bishop@northernneckgarbage.com");
Email emailTo = new Email(to);
      Mail mail = new Mail(from, subject, emailTo, content);
        SendGrid sendGrid = new SendGrid(sendgridApiKey.isEmpty() ? "demo-key" : sendgridApiKey);

        Request request = new Request();
LOGGER.info("Sending email to {}", to);
        try {
          request.setMethod(Method.POST);
          request.setEndpoint("mail/send");
          request.setBody(mail.build());
          Response response = sendGrid.api(request);
            LOGGER.warn("Email sent with status code: {}", response.getStatusCode());
            LOGGER.info("Response Body: {}", response.getBody());
            LOGGER.info("Response Headers: {}", response.getHeaders());
        } catch (IOException ex) {
          LOGGER.error("Failed to send email to {}: {}", to, ex.getMessage());
          throw ex;
        }
    }

    public void sendDirectMessageToSales(String userEmail, String userPhone, String userName, String message) throws IOException {
        String recipientEmail = "bishop@northernneckgarbage.com";
        String subject = "New Message from " + userName;
        String formattedMessage = buildEmailContent(userEmail, userPhone, userName, message);
        Content content = new Content("text/plain", formattedMessage);

        Email from = new Email("noreply@northernneckgarbage.com");
        Email to = new Email(recipientEmail);
        Mail mail = new Mail(from, subject, to, content);
        SendGrid sendGrid = new SendGrid(sendgridApiKey.isEmpty() ? "demo-key" : sendgridApiKey);

        Request request = new Request();
        LOGGER.info("Sending direct message to Sales from: {}", userEmail);
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            LOGGER.info("Email sent with status code: {}", response.getStatusCode());
        } catch (IOException ex) {
            LOGGER.error("Error in sending email: {}", ex.getMessage());
            LOGGER.error(String.valueOf(ex));
            throw ex;
        }
    }

    private String buildEmailContent(String userEmail, String userPhone, String userName, String message) {
        return String.format("User Email: %s%nUser Phone: %s%nUser Name: %s%n%nMessage:%n%s", 
                userEmail, userPhone, userName, message);
    }


}
