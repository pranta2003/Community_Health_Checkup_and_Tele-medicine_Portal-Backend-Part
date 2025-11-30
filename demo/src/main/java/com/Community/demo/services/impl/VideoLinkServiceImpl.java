package com.Community.demo.services.impl;

import com.Community.demo.model.Appointment;
import com.Community.demo.model.ConsultationSession;
import com.Community.demo.repository.AppointmentRepository;
import com.Community.demo.repository.ConsultationRepository;
import com.Community.demo.services.VideoLinkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class VideoLinkServiceImpl implements VideoLinkService {

    @Value("${jitsi.domain:meet.jit.si}")
    private String domain;

    @Value("${jitsi.scheme:https}")
    private String scheme;

    @Value("${jitsi.room.prefix:chc-}")
    private String prefix;

    @Value("${jitsi.prejoin:true}")
    private boolean prejoin;

    private final AppointmentRepository appointmentRepo;
    private final ConsultationRepository consultationRepo;

    public VideoLinkServiceImpl(AppointmentRepository appointmentRepo, ConsultationRepository consultationRepo) {
        this.appointmentRepo = appointmentRepo;
        this.consultationRepo = consultationRepo;
    }

    @Override
    public String ensureRoomForAppointment(Long appointmentId) {
        Appointment appt = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        if (appt.getJitsiRoom() == null || appt.getJitsiRoom().isBlank()) {
            appt.setJitsiRoom(generateRoom());
            appointmentRepo.save(appt);
        }
        return appt.getJitsiRoom();
    }

    @Override
    public String ensureRoomForConsultation(Long consultationId) {
        ConsultationSession cs = consultationRepo.findById(consultationId)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));
        if (cs.getJitsiRoom() == null || cs.getJitsiRoom().isBlank()) {
            cs.setJitsiRoom(generateRoom());
            consultationRepo.save(cs);
        }
        return cs.getJitsiRoom();
    }

    @Override
    public String buildJoinUrl(String room) {
        String base = scheme + "://" + domain + "/" + room;
        // keep it simple; prejoin page helps beginners
        if (prejoin) {
            return base + "#config.prejoinPageEnabled=true";
        }
        return base;
    }

    private String generateRoom() {
        // short, random, predictable prefix
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
