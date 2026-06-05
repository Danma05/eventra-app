package com.eventra.mobile;

public class ActiveParticipant {

    private long activitySessionId;
    private long authUserId;
    private double latitude;
    private double longitude;
    private double speedKmh;
    private int currentPosition;

    public ActiveParticipant(long activitySessionId, long authUserId, double latitude,
                             double longitude, double speedKmh, int currentPosition) {
        this.activitySessionId = activitySessionId;
        this.authUserId = authUserId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speedKmh = speedKmh;
        this.currentPosition = currentPosition;
    }

    public long getActivitySessionId() {
        return activitySessionId;
    }

    public long getAuthUserId() {
        return authUserId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getSpeedKmh() {
        return speedKmh;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }
}