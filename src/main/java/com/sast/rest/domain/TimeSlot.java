package com.sast.rest.domain;

import com.fasterxml.jackson.annotation.*;
import com.sast.rest.domain.enumeration.DayOfWeek;
import com.sast.rest.domain.enumeration.Status;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Access(AccessType.FIELD)
@Table(name = "time_slots", uniqueConstraints = @UniqueConstraint(columnNames = {"person_id", "from", "to", "date"}))
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TimeSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek day;

    @Column(name = "`from`", nullable = false)
    Short from;

    @Column(name = "`to`", nullable = false)
    Short to;

    @ManyToOne(targetEntity=Person.class, fetch=FetchType.LAZY)
    @JoinColumn(name = "person_id")
    @JsonBackReference
    private Person person;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status = Status.FREE;


    public Long getId() {
        return id;
    }

    public TimeSlot setId(Long id) {
        this.id = id;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public TimeSlot setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public TimeSlot setDay(DayOfWeek day) {
        this.day = day;
        return this;
    }

    public Short getFrom() {
        return from;
    }

    public TimeSlot setFrom(Short from) {
        this.from = from;
        return this;
    }

    public Short getTo() {
        return to;
    }

    public TimeSlot setTo(Short to) {
        this.to = to;
        return this;
    }

    public Person getPerson() {
        return person;
    }

    public TimeSlot setPerson(Person person) {
        this.person = person;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public TimeSlot setStatus(Status status) {
        this.status = status;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return Objects.equals(getId(), timeSlot.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "id=" + id +
                ", date=" + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                ", day=" + day +
                ", from=" + from +
                ", to=" + to +
                ", person= {" +
                    ", id=" + person.getId() +
                    ", first_name=" + person.getFirstName() +
                    ", last_name=" + person.getLastName() +
                '}' +
                ", status=" + status +
                '}';
    }
}
