package com.gdsc.solutionchallenge.meetup;

import com.gdsc.solutionchallenge.meetup.dto.LocationDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class GeometryUtils {
    public static double calculateDistanceHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Radius of the earth in meters
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public static LocationDto.Post locationToDto(Point point, double distance, Double bearing) {
        return calculateByDirection(point.getX(), point.getY(), distance, bearing);
    }

    public static Geometry createPoint(double lat, double lng) throws ParseException {
        return new WKTReader().read("POINT(" + lng + " " + lat + ")");
    }


    public static LocationDto.Post calculateByDirection(Double baseLatitude, Double baseLongitude, double distance,
                                                        Double bearing) {

        Double radianLatitude = toRadian(baseLatitude);
        Double radianLongitude = toRadian(baseLongitude);
        Double radianAngle = toRadian(bearing);
        Double distanceRadius = distance / 6371.01;

        Double latitude = Math.asin(sin(radianLatitude) * cos(distanceRadius) +
                cos(radianLatitude) * sin(distanceRadius) * cos(radianAngle));
        Double longitude = radianLongitude + Math.atan2(sin(radianAngle) * sin(distanceRadius) * cos(radianLatitude),
                cos(distanceRadius) - sin(radianLatitude) * sin(latitude));

        longitude = (longitude + 540) % 360 - 180;

        return LocationDto.Post.of(toDegree(latitude), toDegree(longitude));
    }

    private static Double toRadian(Double coordinate) {
        return coordinate * Math.PI / 180.0;
    }

    private static Double toDegree(Double coordinate) {
        return coordinate * 180.0 / Math.PI;
    }

    private static Double sin(Double coordinate) {
        return Math.sin(coordinate);
    }

    private static Double cos(Double coordinate) {
        return Math.cos(coordinate);
    }

    public static Point getEmptyPoint() throws ParseException {
        Double latitude = 0.0;
        Double longitude = 0.0;
        String pointWKT = String.format("POINT(%s %s)", latitude, longitude);
        return (Point) new WKTReader().read(pointWKT);
    }

    public static Point getPoint(LocationDto.Post location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        GeometryFactory gf = new GeometryFactory();
        return gf.createPoint(new Coordinate(latitude, longitude));
    }

    public static Geometry getMBR(Geometry startGeometry, double distance) {
        return startGeometry.buffer(distance);
    }
}