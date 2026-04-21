package com.eventra.mobile;

public class Event {

    private long id;
    private long organizerAuthUserId;
    private String title;
    private String description;
    private String eventDate;
    private String location;
    private int capacity;
    private String status;

    public Event(long id, long organizerAuthUserId, String title, String description,
                 String eventDate, String location, int capacity, String status) {
        this.id = id;
        this.organizerAuthUserId = organizerAuthUserId;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public long getOrganizerAuthUserId() {
        return organizerAuthUserId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getStatus() {
        return status;
    }
}