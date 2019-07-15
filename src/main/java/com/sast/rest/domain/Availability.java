package com.sast.rest.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sast.rest.domain.enumeration.DayOfWeek;

import java.util.List;
import java.util.Objects;

public class Availability {

    private DayOfWeek day;

    private List<Time> times;

    @JsonProperty("person_id")
    private Long personId;

    public DayOfWeek getDay() {
          return day;
    }

    public Availability setDay(DayOfWeek day) {
        this.day = day;
        return this;
    }

    public List<Time> getTimes() {
        return times;
    }

    public Availability setTimes(List<Time> times) {
        this.times = times;
        return this;
    }

    public Long getPersonId() {
        return personId;
    }

    public Availability setPersonId(Long personId) {
        this.personId = personId;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this);
    }

    @Override
    public String toString() {
        return "Availability{" +
                ", day='" + getDay() + "'" +
                ", time='" + getTimes() + "'" +
                "}";
    }
}
