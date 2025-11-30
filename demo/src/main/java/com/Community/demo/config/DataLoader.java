package com.Community.demo.config;




import com.Community.demo.model.Appointment;
import com.Community.demo.model.ConsultationSession;
import com.Community.demo.model.User;
import com.Community.demo.repository.AppointmentRepository;
import com.Community.demo.repository.ConsultationRepository;
import com.Community.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * DataLoader seeds initial data when the application starts (non-test profiles).
 *
 * - It attempts to create a default admin user (email: admin@local).
 * - It creates a sample appointment and a sample consultation session.
 *
 *
 * NOTES:
 * 1) This class expects basic repository interfaces in package com.community.health.repository
 *    with typical Spring Data methods such as count() and save(..).
 * 2) Model classes (User, Appointment, ConsultationSession) are used in an assumed simple form.
 *    If your model fields differ, adjust the setter calls below or tell me the model definitions
 *    and I will update this loader exactly to match them.
 *
 * To disable this loader in production, run the app with profile "prod" or set spring.profiles.active=prod.
 */
@Component
@Profile("!test") // do not run during unit tests
@Slf4j
public class DataLoader implements ApplicationRunner {

//    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final ConsultationRepository consultationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository,
                      AppointmentRepository appointmentRepository,
                      ConsultationRepository consultationRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.consultationRepository = consultationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("DataLoader running — checking for initial data...");

        createAdminUserIfMissing();
        createSampleAppointmentIfMissing();
        createSampleConsultationIfMissing();

        log.info("DataLoader finished.");
    }

    private void createAdminUserIfMissing() {
        try {
            long count = userRepository.count();
            if (count == 0) {
                log.info("No users found — creating default admin user.");

                User admin = new User(); // expects a no-arg constructor
                // Typical fields — change names if your User model uses different setters/names
                try {
                    admin.setName("Admin");
                } catch (NoSuchMethodError | Exception ignored) { /* ignore if setter not present */ }

                try {
                    admin.setEmail("admin@example.com");
                } catch (NoSuchMethodError | Exception ignored) { /* ignore */ }

                // set encoded password if setter present
                try {
                    String raw = "Admin@123";
                    if (passwordEncoder != null) {
                        admin.setPassword(passwordEncoder.encode(raw));
                    } else {
                        admin.setPassword(raw);
                    }
                } catch (NoSuchMethodError | Exception ignored) { /* ignore */ }

                // set role(s) if your model has roles (example: List<String> or Set<String>)
                try {
                    admin.setRoles(Collections.singleton("ROLE_ADMIN"));
                } catch (NoSuchMethodError | Exception ignored) { /* ignore */ }

                // persist admin user
                userRepository.save(admin);
                log.info("Default admin user created: admin@example.com (password: Admin@123). Please change immediately.");            } else {
                log.info("Users already exist (count = {}) — skipping admin creation.", count);
            }
        } catch (Exception ex) {
            log.warn("Could not create admin user automatically. This might mean your User model or repository "
                    + "has different methods/field names. Exception: {}", ex.getMessage());
        }
    }

    private void createSampleAppointmentIfMissing() {
        try {
            long count = appointmentRepository.count();
            if (count == 0) {
                log.info("No appointments found — creating a sample appointment.");

                Appointment appt = new Appointment();
                try {
                    appt.setTitle("Sample Community Health Check");
                } catch (Exception ignored) {}

                try {
                    appt.setScheduledAt(LocalDateTime.now().plusDays(3));
                } catch (Exception ignored) {}

                // link to user if field exists (example)
                try {
                    // If your Appointment has a setPatient(...) method and a User exists, attempt to set it.
                    if (userRepository.count() > 0) {
                        User anyUser = userRepository.findAll().iterator().next();
                        appt.setPatient(anyUser);
                    }
                } catch (Exception ignored) {}

                appointmentRepository.save(appt);
                log.info("Sample appointment created.");
            } else {
                log.info("Appointments already exist (count = {}) — skipping sample appointment.", count);
            }
        } catch (Exception ex) {
            log.warn("Could not create sample appointment automatically: {}", ex.getMessage());
        }
    }

    private void createSampleConsultationIfMissing() {
        try {
            long count = consultationRepository.count();
            if (count == 0) {
                log.info("No consultation sessions found — creating a sample consultation session.");

                ConsultationSession cs = new ConsultationSession();
                try {
                    cs.setTopic("Sample Teleconsultation");
                } catch (Exception ignored) {}

                try {
                    cs.setSessionStart(LocalDateTime.now().plusDays(3));
                    cs.setSessionEnd(LocalDateTime.now().plusDays(3).plusMinutes(30));
                } catch (Exception ignored) {}

              //  catch (Exception ignored) {}

                // attach appointment or user if available

                try {
                    if (appointmentRepository.count() > 0) {
                        Appointment anyAppt = appointmentRepository.findAll().iterator().next();
                        cs.setAppointment(anyAppt);
                    }
                } catch (Exception ignored) {}

                consultationRepository.save(cs);
                log.info("Sample consultation session created.");
            } else {
                log.info("Consultation sessions already exist (count = {}) — skipping sample creation.", count);
            }
        } catch (Exception ex) {
            log.warn("Could not create sample consultation automatically: {}", ex.getMessage());
        }
    }
}
