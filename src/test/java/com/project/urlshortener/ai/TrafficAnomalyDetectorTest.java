package com.project.urlshortener.ai;

import com.project.urlshortener.dto.response.analytics.AnalyticsPointResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrafficAnomalyDetectorTest {

    private final TrafficAnomalyDetector trafficAnomalyDetector = new TrafficAnomalyDetector();

    @Test
    void detectShouldFlagLargeTrafficSpike() {
        TrafficAnomalyObservation observation = trafficAnomalyDetector.detect(List.of(
                point("2026-07-26", 10),
                point("2026-07-27", 11),
                point("2026-07-28", 9),
                point("2026-07-29", 10),
                point("2026-07-30", 12),
                point("2026-07-31", 11),
                point("2026-08-01", 10),
                point("2026-08-02", 35)
        ));

        assertTrue(observation.unusual());
    }

    @Test
    void detectShouldIgnoreStableTraffic() {
        TrafficAnomalyObservation observation = trafficAnomalyDetector.detect(List.of(
                point("2026-07-26", 10),
                point("2026-07-27", 11),
                point("2026-07-28", 9),
                point("2026-07-29", 10),
                point("2026-07-30", 12),
                point("2026-07-31", 11),
                point("2026-08-01", 10),
                point("2026-08-02", 12)
        ));

        assertFalse(observation.unusual());
    }

    private AnalyticsPointResponse point(String label, long count) {
        return new AnalyticsPointResponse(label, count);
    }
}
