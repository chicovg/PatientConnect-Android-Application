package com.chicovg.symptommgmt;

import com.chicovg.symptommgmt.config.WebSecurityConfig;
import com.chicovg.symptommgmt.core.entity.*;
import com.chicovg.symptommgmt.core.repository.DoctorRepository;
import com.chicovg.symptommgmt.core.repository.PatientRepository;
import com.chicovg.symptommgmt.core.repository.UserRepository;
import com.chicovg.symptommgmt.rest.domain.CheckInStatus;
import com.chicovg.symptommgmt.rest.domain.PainLevel;
import com.chicovg.symptommgmt.rest.domain.UserType;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.joda.time.DateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

@Configuration
@Import(WebSecurityConfig.class)
@EnableAutoConfiguration
@EnableJpaRepositories
@ComponentScan
public class Application {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        stageTestData(ctx);
    }

    private static void stageTestData(ApplicationContext ctx){
        UserRepository userRepository = (UserRepository)ctx.getBean("userRepository");

        DoctorRepository doctorRepository = (DoctorRepository)ctx.getBean("doctorRepository");
        DoctorPo doctorPo = getDoctor1();

        doctorPo.addPatient(getPatient1());
        doctorPo.addPatient(getPatient2());
        doctorPo.addPatient(getPatient3());
        doctorPo.addPatient(getPatient4());

        doctorPo = doctorRepository.save(doctorPo);
        userRepository.save(new SymptomMgmtUser("doctor1", new ShaPasswordEncoder().encodePassword("password", null), doctorPo.getId(),  UserType.DOCTOR));
        for(PatientPo po : doctorPo.getPatientPoList()){
            userRepository.save(new SymptomMgmtUser(po.getUsername(), new ShaPasswordEncoder().encodePassword("password", null), po.getId(), UserType.PATIENT));
        }

        for (SymptomMgmtUser user : userRepository.findAll()){
            Logger.getLogger("Application").info("user: " + user.getUsername());
        }
        for(DoctorPo doc : doctorRepository.findAll()){
            Logger.getLogger("Application").info("doctor: " + doc.getUsername());
        }
        for(PatientPo pat : ((PatientRepository)ctx.getBean("patientRepository")).findAll()){
            Logger.getLogger("Application").info("patient: " + pat.getUsername());
        }
    }

    private static DoctorPo getDoctor1(){
        DoctorPo doctorPo = new DoctorPo();
        doctorPo.setLastUpdtDatetime(new Date());
        doctorPo.setFirstName("Mike");
        doctorPo.setLastName("Who");
        doctorPo.setEmailAddress("doctor.who@gmail.com");
        doctorPo.setPhoneNumber("717-576-8206");
        doctorPo.setUsername("doctor1");
        return doctorPo;
    }

    private static PatientPo getPatient1(){
        PatientPo patient = new PatientPo();
        patient.setFirstName("Bob");
        patient.setLastName("Smothers");
        patient.setEmailAddress("bob.smothers@gmail.com");
        patient.setPhoneNumber("303-123-4578");
        patient.setDateOfBirth(new DateTime(1970, 3, 2, 0, 0, 0, 0).toDate());
        patient.setLastUpdtDatetime(new Date());
        patient.setUsername("patient1");
        patient.setPoints(0L);
        patient.setPointsThisMonth(0L);
        patient.setPointsThisWeek(0L);

        //TimeZone tz = TimeZone.getDefault();
        PatientCheckinPo checkIn1 = new PatientCheckinPo(new DateTime().minusHours(2).toDate(), CheckInStatus.COMPLETED.toString(), PainLevel.MODERATE.toString());
        checkIn1.addResponse(new PatientCheckinResponsePo(1, "MODERATE"));
        checkIn1.addResponse(new PatientCheckinResponsePo(2, "yes"));
        checkIn1.addResponse(new PatientCheckinResponsePo(3, "no", "OxyContin"));
        checkIn1.addResponse(new PatientCheckinResponsePo(3, "yes", new PatientMedicationTakenPo(new DateTime().minusHours(2).toDate(), "Tylenol")));
        checkIn1.addResponse(new PatientCheckinResponsePo(4, "no"));
        patient.addCheckIn(checkIn1);
        patient.setLastPainLevel(checkIn1.getOverallPainLevel());
        patient.setLastPainLevelReportedDtm(checkIn1.getTimestamp());
        patient.setLastPainLevelChangedDtm(checkIn1.getTimestamp());

        PatientCheckinPo checkIn2 = new PatientCheckinPo(new DateTime().minusHours(8).toDate(), CheckInStatus.COMPLETED.toString(), PainLevel.SEVERE.toString());
        checkIn2.addResponse(new PatientCheckinResponsePo(1, "SEVERE"));
        checkIn2.addResponse(new PatientCheckinResponsePo(2, "yes"));
        checkIn2.addResponse(new PatientCheckinResponsePo(3, "no", "OxyContin"));
        checkIn2.addResponse(new PatientCheckinResponsePo(3, "yes", new PatientMedicationTakenPo(new DateTime().minusHours(8).toDate(), "Tylenol")));
        checkIn2.addResponse(new PatientCheckinResponsePo(4, "I can\'t eat"));
        patient.addCheckIn(checkIn2);

        PatientCheckinPo checkIn3 = new PatientCheckinPo(new DateTime().minusHours(24).toDate(), CheckInStatus.COMPLETED.toString(), PainLevel.WELL_CONTROLLED.toString());
        checkIn3.addResponse(new PatientCheckinResponsePo(1, "MODERATE"));
        checkIn3.addResponse(new PatientCheckinResponsePo(2, "yes"));
        checkIn3.addResponse(new PatientCheckinResponsePo(3, "yes", new PatientMedicationTakenPo(new DateTime().minusHours(24).toDate(), "OxyContin")));
        checkIn3.addResponse(new PatientCheckinResponsePo(3, "yes", new PatientMedicationTakenPo(new DateTime().minusHours(24).toDate(), "Tylenol")));
        checkIn3.addResponse(new PatientCheckinResponsePo(4, "no"));
        patient.addCheckIn(checkIn3);

        PatientCheckinPo checkIn4 = new PatientCheckinPo(new DateTime().minusHours(32).toDate(), CheckInStatus.COMPLETED.toString(), PainLevel.MODERATE.toString());
        checkIn4.addResponse(new PatientCheckinResponsePo(1, "MODERATE"));
        checkIn4.addResponse(new PatientCheckinResponsePo(2, "yes"));
        checkIn4.addResponse(new PatientCheckinResponsePo(3, "no", "OxyContin"));
        checkIn4.addResponse(new PatientCheckinResponsePo(3, "yes", new PatientMedicationTakenPo(new DateTime().minusHours(24).toDate(), "Tylenol")));
        checkIn4.addResponse(new PatientCheckinResponsePo(4, "no"));
        patient.addCheckIn(checkIn4);

        patient.addReminder(new PatientReminderPo(8, 0));
        patient.addReminder(new PatientReminderPo(12, 00));
        patient.addReminder(new PatientReminderPo(16, 00));
        patient.addReminder(new PatientReminderPo(20, 00));

        patient.addMedication(new PatientMedicationPo("OxyContin", 10, "mg", "Take once every 12 hours"));
        patient.addMedication(new PatientMedicationPo("Tylenol", 325, "mg", "Take one tablet every 4 to 6 hours"));

        return patient;
    }

    private static PatientPo getPatient2(){
        PatientPo patient = new PatientPo();
        patient.setFirstName("Bob");
        patient.setLastName("Buckley");
        patient.setEmailAddress("buckko213@gmail.com");
        patient.setPhoneNumber("441-658-2222");
        patient.setDateOfBirth(new DateTime(1989, 1, 12, 0, 0, 0, 0).toDate());
        patient.setLastUpdtDatetime(new Date());
        patient.setUsername("patient2");
        patient.setPoints(0L);
        patient.setPointsThisMonth(0L);
        patient.setPointsThisWeek(0L);

        //TimeZone tz = TimeZone.getDefault();
        PatientCheckinPo checkIn1 = new PatientCheckinPo(new DateTime().minusHours(8).toDate(), CheckInStatus.COMPLETED.toString(), PainLevel.SEVERE.toString());
        checkIn1.addResponse(new PatientCheckinResponsePo(1, "SEVERE"));
        checkIn1.addResponse(new PatientCheckinResponsePo(2, "yes"));
        checkIn1.addResponse(new PatientCheckinResponsePo(3, "yes", new PatientMedicationTakenPo(new DateTime().minusHours(8).toDate(), "Lortab")));
        checkIn1.addResponse(new PatientCheckinResponsePo(3, "yes", new PatientMedicationTakenPo(new DateTime().minusHours(8).toDate(), "Advil")));
        checkIn1.addResponse(new PatientCheckinResponsePo(4, "I can\'t eat"));
        patient.addCheckIn(checkIn1);
        patient.setLastPainLevel(checkIn1.getOverallPainLevel());
        patient.setLastPainLevelReportedDtm(checkIn1.getTimestamp());

        PatientCheckinPo checkIn2 = new PatientCheckinPo(new DateTime().minusHours(14).toDate(), CheckInStatus.COMPLETED.toString(), PainLevel.SEVERE.toString());
        checkIn2.addResponse(new PatientCheckinResponsePo(1, "SEVERE"));
        checkIn2.addResponse(new PatientCheckinResponsePo(2, "yes"));
        checkIn2.addResponse(new PatientCheckinResponsePo(3, "yes", new PatientMedicationTakenPo(new DateTime().minusHours(14).toDate(), "Lortab")));
        checkIn2.addResponse(new PatientCheckinResponsePo(3, "no", "Advil"));
        checkIn2.addResponse(new PatientCheckinResponsePo(4, "I can\'t eat"));
        patient.addCheckIn(checkIn2);
        patient.setLastPainLevelChangedDtm(checkIn2.getTimestamp());

        PatientCheckinPo checkIn3 = new PatientCheckinPo(new DateTime().minusHours(24).toDate(), CheckInStatus.COMPLETED.toString(), PainLevel.WELL_CONTROLLED.toString());
        checkIn3.addResponse(new PatientCheckinResponsePo(1, "WELL_CONTROLLED"));
        checkIn3.addResponse(new PatientCheckinResponsePo(2, "yes"));
        checkIn3.addResponse(new PatientCheckinResponsePo(3, "no", new PatientMedicationTakenPo(new DateTime().minusHours(24).toDate(), "Lortab")));
        checkIn3.addResponse(new PatientCheckinResponsePo(3, "no", new PatientMedicationTakenPo(new DateTime().minusHours(24).toDate(), "Advil")));
        checkIn3.addResponse(new PatientCheckinResponsePo(4, "no"));
        patient.addCheckIn(checkIn3);

        patient.addReminder(new PatientReminderPo(8, 0));
        patient.addReminder(new PatientReminderPo(12, 00));
        patient.addReminder(new PatientReminderPo(16, 00));
        patient.addReminder(new PatientReminderPo(20, 00));

        patient.addMedication(new PatientMedicationPo("Lortab", 325, "mg", "Take one tablet every 4 to 6 hours"));
        patient.addMedication(new PatientMedicationPo("Advil", 800, "mg", "Take up to 4 times per day"));

        return patient;
    }

    private static PatientPo getPatient3(){
        PatientPo patient = new PatientPo();
        patient.setFirstName("Lucy");
        patient.setLastName("Schlemmer");
        patient.setEmailAddress("lschlemm@gmail.com");
        patient.setPhoneNumber("121-432-0981");
        patient.setDateOfBirth(new DateTime(1981, 8, 22, 0, 0, 0, 0).toDate());
        patient.setLastUpdtDatetime(new Date());
        patient.setUsername("patient3");
        patient.setPoints(0L);
        patient.setPointsThisMonth(0L);
        patient.setPointsThisWeek(0L);

        //TimeZone tz = TimeZone.getDefault();
        PatientCheckinPo checkIn1 = new PatientCheckinPo(new DateTime().minusHours(12).toDate(), CheckInStatus.COMPLETED.toString(), PainLevel.WELL_CONTROLLED.toString());
        checkIn1.addResponse(new PatientCheckinResponsePo(1, "WELL_CONTROLLED"));
        checkIn1.addResponse(new PatientCheckinResponsePo(2, "yes"));
        checkIn1.addResponse(new PatientCheckinResponsePo(3, "no", "Lortab"));
        checkIn1.addResponse(new PatientCheckinResponsePo(3, "no", "Advil"));
        checkIn1.addResponse(new PatientCheckinResponsePo(3, "no", "Roxicodone"));
        checkIn1.addResponse(new PatientCheckinResponsePo(4, "no"));
        patient.addCheckIn(checkIn1);

        patient.setLastPainLevel(checkIn1.getOverallPainLevel());
        patient.setLastPainLevelReportedDtm(checkIn1.getTimestamp());
        patient.setLastPainLevelChangedDtm(checkIn1.getTimestamp());

        patient.addReminder(new PatientReminderPo(8, 0));
        patient.addReminder(new PatientReminderPo(12, 00));
        patient.addReminder(new PatientReminderPo(16, 00));
        patient.addReminder(new PatientReminderPo(20, 00));

        patient.addMedication(new PatientMedicationPo("Lortab", 325, "mg", "Take one tablet every 4 to 6 hours"));
        patient.addMedication(new PatientMedicationPo("Advil", 800, "mg", "Take up to 4 times per day"));
        patient.addMedication(new PatientMedicationPo("Roxicodone", 25, "mg", "Take every 4 to 6 hours as needed"));

        return patient;
    }

    private static PatientPo getPatient4(){
        PatientPo patient = new PatientPo();
        patient.setFirstName("Moe");
        patient.setLastName("Paper");
        patient.setEmailAddress("needmopaper@gmail.com");
        patient.setPhoneNumber("222-333-4444");
        patient.setDateOfBirth(new DateTime(1967, 6, 6, 0, 0, 0, 0).toDate());
        patient.setLastUpdtDatetime(new Date());
        patient.setUsername("patient4");
        patient.setPoints(0L);
        patient.setPointsThisMonth(0L);
        patient.setPointsThisWeek(0L);

        patient.addReminder(new PatientReminderPo(8, 0));
        patient.addReminder(new PatientReminderPo(12, 00));
        patient.addReminder(new PatientReminderPo(16, 00));
        patient.addReminder(new PatientReminderPo(20, 00));

        patient.addMedication(new PatientMedicationPo("Advil", 800, "mg", "Take up to 4 times per day"));

        return patient;
    }

    //@Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        tomcat.addAdditionalTomcatConnectors(createSslConnector());
        return tomcat;
    }

    private Connector createSslConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        try {
            File keystore = new ClassPathResource("tomcat.keystore").getFile();
            File truststore = new ClassPathResource("tomcat.keystore").getFile();
            connector.setScheme("https");
            connector.setSecure(true);
            connector.setPort(8443);
            protocol.setSSLEnabled(true);
            protocol.setKeystoreFile(keystore.getAbsolutePath());
            protocol.setKeystorePass("password");
            protocol.setTruststoreFile(truststore.getAbsolutePath());
            protocol.setTruststorePass("password");
            protocol.setKeyAlias("tomcat");
            return connector;
        }
        catch (IOException ex) {
            throw new IllegalStateException("can't access keystore: [" + "keystore"
                    + "] or truststore: [" + "keystore" + "]", ex);
        }
    }

}

/*
Advil, Motrin, Tylenol

Avinza, Ms Contin, Oxycontin, Roxicodone, Dilaudid, Exalgo, Actiq, Dolophine

 */