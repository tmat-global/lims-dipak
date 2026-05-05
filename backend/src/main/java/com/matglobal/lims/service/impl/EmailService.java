package com.matglobal.lims.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.lab-name}")
    private String labName;

    // ══════════════════════════════════════════════════════
    // TRIGGER 1 — On Patient Registration
    // ══════════════════════════════════════════════════════
    @Async
    public void sendRegistrationConfirmation(
            String toEmail,
            String patientName,
            String regNo,
            String tests,
            String totalAmount,
            String refDoctor) {

        if (toEmail == null || toEmail.isBlank()) {
            log.warn("Skipping registration email — no email for patient {}", patientName);
            return;
        }

        String subject = labName + " — Registration Confirmed [" + regNo + "]";
        String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"));

        String body = buildRegistrationEmailHtml(
                patientName, regNo, tests, totalAmount, refDoctor, now);

        sendHtmlEmail(toEmail, subject, body);
    }

    // ══════════════════════════════════════════════════════
    // TRIGGER 2 — On Report Ready
    // ══════════════════════════════════════════════════════
    @Async
    public void sendReportReadyNotification(
            String toEmail,
            String patientName,
            String regNo,
            String testName) {

        if (toEmail == null || toEmail.isBlank()) {
            log.warn("Skipping report-ready email — no email for patient {}", patientName);
            return;
        }

        String subject = labName + " — Your Report is Ready [" + regNo + "]";
        String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"));

        String body = buildReportReadyEmailHtml(patientName, regNo, testName, now);

        sendHtmlEmail(toEmail, subject, body);
    }

    // ══════════════════════════════════════════════════════
    // CORE SEND METHOD
    // ══════════════════════════════════════════════════════
    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, true, "UTF-8");
            helper.setFrom(fromEmail, labName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("✅ Email sent to {} | Subject: {}", to, subject);
        } catch (MessagingException e) {
            log.error("❌ Failed to send email to {}: {}", to, e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected email error: {}", e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════
    // EMAIL TEMPLATE 1 — Registration Confirmation
    // ══════════════════════════════════════════════════════
    private String buildRegistrationEmailHtml(
            String patientName,
            String regNo,
            String tests,
            String totalAmount,
            String refDoctor,
            String dateTime) {

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <style>
                    body { margin:0; padding:0; background:#f0f5f9;
                           font-family:'Segoe UI',Arial,sans-serif; }
                    .wrap { max-width:600px; margin:30px auto;
                            background:#fff; border-radius:12px;
                            overflow:hidden;
                            box-shadow:0 4px 16px rgba(0,0,0,0.10); }
                    .header { background:#0d2137; padding:28px 32px;
                              text-align:center; }
                    .header h1 { color:#fff; margin:0; font-size:22px;
                                 letter-spacing:0.5px; }
                    .header p  { color:#72add1; margin:6px 0 0;
                                 font-size:13px; }
                    .badge { display:inline-block; background:#204e7f;
                             color:#fff; padding:6px 18px;
                             border-radius:20px; font-size:13px;
                             font-weight:600; margin-top:12px; }
                    .body  { padding:28px 32px; }
                    .greeting { font-size:16px; color:#0a1929;
                                font-weight:600; margin-bottom:8px; }
                    .text  { font-size:13.5px; color:#2d4a6b;
                             line-height:1.7; margin-bottom:20px; }
                    .info-box { background:#f0f5f9; border-radius:8px;
                                padding:18px 20px; margin-bottom:20px;
                                border-left:4px solid #4682b4; }
                    .info-row { display:flex; justify-content:space-between;
                                padding:6px 0;
                                border-bottom:1px solid #dce8f0;
                                font-size:13px; }
                    .info-row:last-child { border-bottom:none; }
                    .info-label { color:#5a7a9a; font-weight:500; }
                    .info-value { color:#0a1929; font-weight:700; }
                    .notice { background:#fef3c7; border:1px solid #fde68a;
                              border-radius:8px; padding:14px 18px;
                              font-size:12.5px; color:#92400e;
                              margin-bottom:20px; }
                    .footer { background:#0d2137; padding:18px 32px;
                              text-align:center; }
                    .footer p { color:#72add1; font-size:12px; margin:0; }
                  </style>
                </head>
                <body>
                  <div class="wrap">
                    <div class="header">
                      <h1>%s</h1>
                      <p>Laboratory Information System</p>
                      <div class="badge">Registration Confirmed</div>
                    </div>
                    <div class="body">
                      <div class="greeting">Dear %s,</div>
                      <div class="text">
                        Your registration has been successfully completed at
                        <strong>%s</strong>. Below are your registration details:
                      </div>
                      <div class="info-box">
                        <div class="info-row">
                          <span class="info-label">Registration No.</span>
                          <span class="info-value">%s</span>
                        </div>
                        <div class="info-row">
                          <span class="info-label">Date &amp; Time</span>
                          <span class="info-value">%s</span>
                        </div>
                        <div class="info-row">
                          <span class="info-label">Tests Ordered</span>
                          <span class="info-value">%s</span>
                        </div>
                        <div class="info-row">
                          <span class="info-label">Total Amount</span>
                          <span class="info-value">₹ %s</span>
                        </div>
                        <div class="info-row">
                          <span class="info-label">Referring Doctor</span>
                          <span class="info-value">%s</span>
                        </div>
                      </div>
                      <div class="notice">
                        📋 Please keep this registration number for future reference.
                        You will be notified once your reports are ready.
                      </div>
                      <div class="text">
                        For any queries, please contact our front desk.
                        Thank you for choosing <strong>%s</strong>.
                      </div>
                    </div>
                    <div class="footer">
                      <p>%s &nbsp;·&nbsp; This is an automated email, please do not reply.</p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(
                labName, patientName, labName,
                regNo, dateTime, tests,
                totalAmount,
                (refDoctor == null || refDoctor.isBlank()) ? "Self Requested" : refDoctor,
                labName, labName);
    }

    // ══════════════════════════════════════════════════════
    // EMAIL TEMPLATE 2 — Report Ready
    // ══════════════════════════════════════════════════════
    private String buildReportReadyEmailHtml(
            String patientName,
            String regNo,
            String testName,
            String dateTime) {

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <style>
                    body { margin:0; padding:0; background:#f0f5f9;
                           font-family:'Segoe UI',Arial,sans-serif; }
                    .wrap { max-width:600px; margin:30px auto;
                            background:#fff; border-radius:12px;
                            overflow:hidden;
                            box-shadow:0 4px 16px rgba(0,0,0,0.10); }
                    .header { background:#15803d; padding:28px 32px;
                              text-align:center; }
                    .header h1 { color:#fff; margin:0; font-size:22px; }
                    .header p  { color:#bbf7d0; margin:6px 0 0;
                                 font-size:13px; }
                    .badge { display:inline-block; background:#166534;
                             color:#fff; padding:6px 18px;
                             border-radius:20px; font-size:13px;
                             font-weight:600; margin-top:12px; }
                    .body  { padding:28px 32px; }
                    .greeting { font-size:16px; color:#0a1929;
                                font-weight:600; margin-bottom:8px; }
                    .text  { font-size:13.5px; color:#2d4a6b;
                             line-height:1.7; margin-bottom:20px; }
                    .info-box { background:#f0fdf4; border-radius:8px;
                                padding:18px 20px; margin-bottom:20px;
                                border-left:4px solid #15803d; }
                    .info-row { display:flex; justify-content:space-between;
                                padding:6px 0;
                                border-bottom:1px solid #dcfce7;
                                font-size:13px; }
                    .info-row:last-child { border-bottom:none; }
                    .info-label { color:#5a7a9a; font-weight:500; }
                    .info-value { color:#0a1929; font-weight:700; }
                    .notice { background:#dcfce7; border:1px solid #bbf7d0;
                              border-radius:8px; padding:14px 18px;
                              font-size:12.5px; color:#15803d;
                              margin-bottom:20px; }
                    .footer { background:#0d2137; padding:18px 32px;
                              text-align:center; }
                    .footer p { color:#72add1; font-size:12px; margin:0; }
                  </style>
                </head>
                <body>
                  <div class="wrap">
                    <div class="header">
                      <h1>%s</h1>
                      <p>Laboratory Information System</p>
                      <div class="badge">✅ Report Ready</div>
                    </div>
                    <div class="body">
                      <div class="greeting">Dear %s,</div>
                      <div class="text">
                        Great news! Your test report is now ready at
                        <strong>%s</strong>.
                      </div>
                      <div class="info-box">
                        <div class="info-row">
                          <span class="info-label">Registration No.</span>
                          <span class="info-value">%s</span>
                        </div>
                        <div class="info-row">
                          <span class="info-label">Test Name</span>
                          <span class="info-value">%s</span>
                        </div>
                        <div class="info-row">
                          <span class="info-label">Ready On</span>
                          <span class="info-value">%s</span>
                        </div>
                      </div>
                      <div class="notice">
                        📋 Please visit the lab with your registration number
                        or contact the front desk to collect your report.
                      </div>
                      <div class="text">
                        Thank you for choosing <strong>%s</strong>.
                      </div>
                    </div>
                    <div class="footer">
                      <p>%s &nbsp;·&nbsp; This is an automated email, please do not reply.</p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(
                labName, patientName, labName,
                regNo, testName, dateTime,
                labName, labName);
    }
}