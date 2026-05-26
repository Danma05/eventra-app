package com.eventra.mobile;

public class Result {

    private long id, eventId, authUserId;
    private int position, totalTimeSeconds, paceSecondsPerKm;
    private double distanceKm;
    private String bibNumber, category, runnerName;

    public Result(long id, long eventId, long authUserId, int position,
                  String bibNumber, String category, int totalTimeSeconds,
                  int paceSecondsPerKm, double distanceKm, String runnerName) {
        this.id = id;
        this.eventId = eventId;
        this.authUserId = authUserId;
        this.position = position;
        this.bibNumber = bibNumber;
        this.category = category;
        this.totalTimeSeconds = totalTimeSeconds;
        this.paceSecondsPerKm = paceSecondsPerKm;
        this.distanceKm = distanceKm;
        this.runnerName = runnerName;
    }

    public long getId() { return id; }
    public long getEventId() { return eventId; }
    public long getAuthUserId() { return authUserId; }
    public int getPosition() { return position; }
    public String getBibNumber() { return bibNumber; }
    public String getCategory() { return category; }
    public int getTotalTimeSeconds() { return totalTimeSeconds; }
    public int getPaceSecondsPerKm() { return paceSecondsPerKm; }
    public double getDistanceKm() { return distanceKm; }
    public String getRunnerName() { return runnerName; }
}