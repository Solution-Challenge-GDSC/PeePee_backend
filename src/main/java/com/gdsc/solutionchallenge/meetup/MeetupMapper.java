package com.gdsc.solutionchallenge.meetup;

import com.gdsc.solutionchallenge.meetup.dto.LocationDto;
import com.gdsc.solutionchallenge.meetup.entity.Location;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;

public interface MeetupMapper {
    default Location postToLocation(LocationDto.Post post) throws ParseException {
        if (post == null) {
            return null;
        }

        Location.LocationBuilder location = Location.builder();

        Point point = GeometryUtils.getPoint(post);

        location.location(point);

        return location.build();
    }

    default LocationDto.Response locationToResponse(Location location) {
        if (location == null) {
            return null;
        }

        LocationDto.Response.ResponseBuilder response = LocationDto.Response.builder();

        response.longitude(location.getLocation().getY());
        response.latitude(location.getLocation().getX());

        return response.build();
    }
}
