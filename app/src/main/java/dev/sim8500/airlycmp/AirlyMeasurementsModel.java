package dev.sim8500.airlycmp;

/**
 * Created by sbernad on 27/08/2017.
 */

public class AirlyMeasurementsModel {

    public class MeasurementsData {
        public float airQualityIndex;
        public float humidity;
        public String measurementTime;
        public float pm1;
        public float pm10;
        public float pm25;
        public float pollutionLevel;
    }

    public MeasurementsData currentMeasurements;

    public String toString()
    {
        return String.format("AirQuality: %f\n\n PM10: %f\n PM2.5: %f\n\n PollutionLevel: %f",
                                this.currentMeasurements.airQualityIndex,
                                this.currentMeasurements.pm10,
                                this.currentMeasurements.pm25,
                                this.currentMeasurements.pollutionLevel);
    }
}
