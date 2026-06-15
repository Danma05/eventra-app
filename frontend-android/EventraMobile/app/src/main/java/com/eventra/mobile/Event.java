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
    private String raceStatus;
    private String imageUrl;
    private boolean resultsPublished;

    // Constructor antiguo: se deja para no romper EventsActivity, EventsFragment u otras clases
    public Event(long id, long organizerAuthUserId, String title, String description,
                 String eventDate, String location, int capacity, String status,
                 String imageUrl) {
        this(id, organizerAuthUserId, title, description, eventDate, location,
                capacity, status, "CREATED", imageUrl);
    }

    // Constructor nuevo con raceStatus
    public Event(long id, long organizerAuthUserId, String title, String description,
                 String eventDate, String location, int capacity, String status,
                 String raceStatus, String imageUrl) {
        this.id = id;
        this.organizerAuthUserId = organizerAuthUserId;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
        this.raceStatus = raceStatus;
        this.imageUrl = imageUrl;
        this.resultsPublished = false;
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

    public String getRaceStatus() {
        return raceStatus;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isResultsPublished() {
        return resultsPublished;
    }

    public void setResultsPublished(boolean resultsPublished) {
        this.resultsPublished = resultsPublished;
    }

    public void setRaceStatus(String raceStatus) {
        this.raceStatus = raceStatus;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}