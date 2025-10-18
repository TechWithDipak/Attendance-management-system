package com.attendance.util;

public final class LocationValidator {
    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    private LocationValidator() {}

    public static double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }

    public static boolean isWithinMeters(double lat1, double lon1, double lat2, double lon2, double thresholdMeters) {
        return haversineMeters(lat1, lon1, lat2, lon2) <= thresholdMeters;
    }
}


