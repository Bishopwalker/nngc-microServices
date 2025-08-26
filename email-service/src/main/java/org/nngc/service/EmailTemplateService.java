package org.nngc.service;

import com.sendgrid.Content;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {
    
    public Content buildRegistrationEmail(String name, String link) {
        String htmlContent = """
            <div style="font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c">
                <table role="presentation" width="100%%" style="border-collapse:collapse;min-width:100%%;width:100%%!important" cellpadding="0" cellspacing="0" border="0">
                    <tbody>
                        <tr>
                            <td width="100%%" height="53" bgcolor="#0b0c0c">
                                <table role="presentation" width="100%%" style="border-collapse:collapse;max-width:580px" cellpadding="0" cellspacing="0" border="0" align="center">
                                    <tbody>
                                        <tr>
                                            <td style="font-size:28px;line-height:1.315789474;padding:20px">
                                                <span style="font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none">
                                                    Confirm Your Email
                                                </span>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                    </tbody>
                </table>
                
                <table role="presentation" align="center" cellpadding="0" cellspacing="0" border="0" style="border-collapse:collapse;max-width:580px;width:100%%!important" width="100%%">
                    <tbody>
                        <tr>
                            <td height="30"><br></td>
                        </tr>
                        <tr>
                            <td style="font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px;padding:0 20px">
                                <p style="margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c">Hi %s,</p>
                                <p style="margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c">
                                    Thank you for registering with Northern Neck Garbage Collection. Please click the link below to activate your account:
                                </p>
                                <p style="margin:0 0 20px 0">
                                    <a href="%s" style="background-color:#1D70B8;color:white;padding:15px 25px;text-decoration:none;border-radius:4px;display:inline-block">
                                        Activate Account
                                    </a>
                                </p>
                                <p style="margin:0 0 20px 0;font-size:14px;line-height:20px;color:#666">
                                    This link will expire in 24 hours for security reasons.
                                </p>
                                <p style="margin:0 0 20px 0;font-size:14px;line-height:20px;color:#666">
                                    If you didn't create an account, please ignore this email.
                                </p>
                                <hr style="margin:30px 0;border:none;border-top:1px solid #eee">
                                <p style="font-size:12px;color:#999;margin:0">
                                    Northern Neck Garbage Collection, LLC<br>
                                    164 Cellar Haven Lane<br>
                                    Lottsburg, VA 22511<br>
                                    Phone: 804-220-0029
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td height="30"><br></td>
                        </tr>
                    </tbody>
                </table>
            </div>
            """.formatted(name, link);
        
        return new Content("text/html", htmlContent);
    }
    
    public Content buildPasswordResetEmail(String name, String link) {
        String htmlContent = """
            <div style="font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c">
                <table role="presentation" width="100%%" style="border-collapse:collapse;min-width:100%%;width:100%%!important" cellpadding="0" cellspacing="0" border="0">
                    <tbody>
                        <tr>
                            <td width="100%%" height="53" bgcolor="#0b0c0c">
                                <table role="presentation" width="100%%" style="border-collapse:collapse;max-width:580px" cellpadding="0" cellspacing="0" border="0" align="center">
                                    <tbody>
                                        <tr>
                                            <td style="font-size:28px;line-height:1.315789474;padding:20px">
                                                <span style="font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none">
                                                    Reset Your Password
                                                </span>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                    </tbody>
                </table>
                
                <table role="presentation" align="center" cellpadding="0" cellspacing="0" border="0" style="border-collapse:collapse;max-width:580px;width:100%%!important" width="100%%">
                    <tbody>
                        <tr>
                            <td height="30"><br></td>
                        </tr>
                        <tr>
                            <td style="font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px;padding:0 20px">
                                <p style="margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c">Hi %s,</p>
                                <p style="margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c">
                                    We received a request to reset your password. Click the link below to create a new password:
                                </p>
                                <p style="margin:0 0 20px 0">
                                    <a href="%s" style="background-color:#1D70B8;color:white;padding:15px 25px;text-decoration:none;border-radius:4px;display:inline-block">
                                        Reset Password
                                    </a>
                                </p>
                                <p style="margin:0 0 20px 0;font-size:14px;line-height:20px;color:#666">
                                    This link will expire in 1 hour for security reasons.
                                </p>
                                <p style="margin:0 0 20px 0;font-size:14px;line-height:20px;color:#666">
                                    If you didn't request this password reset, please ignore this email.
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td height="30"><br></td>
                        </tr>
                    </tbody>
                </table>
            </div>
            """.formatted(name, link);
        
        return new Content("text/html", htmlContent);
    }
    
    public Content buildWelcomeEmail(String name) {
        String htmlContent = """
            <div style="font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c">
                <table role="presentation" width="100%%" style="border-collapse:collapse;min-width:100%%;width:100%%!important" cellpadding="0" cellspacing="0" border="0">
                    <tbody>
                        <tr>
                            <td width="100%%" height="53" bgcolor="#0b0c0c">
                                <table role="presentation" width="100%%" style="border-collapse:collapse;max-width:580px" cellpadding="0" cellspacing="0" border="0" align="center">
                                    <tbody>
                                        <tr>
                                            <td style="font-size:28px;line-height:1.315789474;padding:20px">
                                                <span style="font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none">
                                                    Welcome to NNGC!
                                                </span>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                    </tbody>
                </table>
                
                <table role="presentation" align="center" cellpadding="0" cellspacing="0" border="0" style="border-collapse:collapse;max-width:580px;width:100%%!important" width="100%%">
                    <tbody>
                        <tr>
                            <td height="30"><br></td>
                        </tr>
                        <tr>
                            <td style="font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px;padding:0 20px">
                                <p style="margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c">Hi %s,</p>
                                <p style="margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c">
                                    Welcome to Northern Neck Garbage Collection! Your account has been successfully activated.
                                </p>
                                <p style="margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c">
                                    You can now log in to your account and manage your waste collection services.
                                </p>
                                <p style="margin:0 0 20px 0">
                                    <a href="https://northernneckgarbage.com/login" style="background-color:#1D70B8;color:white;padding:15px 25px;text-decoration:none;border-radius:4px;display:inline-block">
                                        Log In to Your Account
                                    </a>
                                </p>
                                <p style="margin:0 0 20px 0;font-size:14px;line-height:20px;color:#666">
                                    Thank you for choosing Northern Neck Garbage Collection!
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td height="30"><br></td>
                        </tr>
                    </tbody>
                </table>
            </div>
            """.formatted(name);
        
        return new Content("text/html", htmlContent);
    }
}