package com.sast.rest.domain;

import java.util.Objects;

public class Time {

    private Short from;
    private Short to;

    public Short getFrom() {
        return from;
    }

    public Time setFrom(Short from) {
        this.from = from;
        return this;
    }

    public Short getTo() {
        return to;
    }

    public Time setTo(Short to) {
        this.to = to;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this);
    }

    @Override
    public String toString() {
        return "Time {" +
                ", from='" + getFrom() + "'" +
                ", to='" + getTo() + "'" +
                "}";
    }
}
