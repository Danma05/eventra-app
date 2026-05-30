package com.eventra.mobile;

public class RegisteredEvent {

    private long eventId;
    private String title;
    private String eventDate;
    private String location;
    private String registrationStatus;

    public RegisteredEvent(long eventId, String title, String eventDate, String location, String registrationStatus) {
        this.eventId = eventId;
        this.title = title;
        this.eventDate = eventDate;
        this.location = location;
        this.registrationStatus = registrationStatus;
    }

    public long getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getLocation() {
        return location;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }
}