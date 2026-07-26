package com.project.urlshortener.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static Instant nowUtc() {
        return Instant.now();
    }

    public static ZonedDateTime toZonedDateTime(Instant instant, String zoneId) {
        return instant.atZone(ZoneId.of(zoneId));
    }
}
