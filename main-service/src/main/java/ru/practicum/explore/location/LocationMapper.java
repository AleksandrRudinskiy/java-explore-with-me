package ru.practicum.explore.location;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LocationMapper {
    public static LocationDto convertToLocationDto(Location location) {
        return new LocationDto(
                location.getLat(),
                location.getLon()
        );
    }
}
