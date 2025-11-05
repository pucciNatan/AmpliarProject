package com.example.ampliar.model;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class PsychologistWorkingHour {

    @Column(name = "day_of_week", nullable = false, length = 20)
    private String dayOfWeek;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    public PsychologistWorkingHour(String dayOfWeek, LocalTime startTime, LocalTime endTime, boolean enabled) {
        setDayOfWeek(dayOfWeek);
        this.startTime = startTime;
        this.endTime = endTime;
        this.enabled = enabled;
    }

    public void setDayOfWeek(String dayOfWeek) {
        if (dayOfWeek == null || dayOfWeek.trim().isEmpty()) {
            throw new IllegalArgumentException("O dia da semana é obrigatório");
        }
        this.dayOfWeek = dayOfWeek.trim().toUpperCase();
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
