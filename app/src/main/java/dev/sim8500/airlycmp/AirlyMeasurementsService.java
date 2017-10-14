package dev.sim8500.airlycmp;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by sbernad on 27/08/2017.
 */

public interface AirlyMeasurementsService {

    @GET("/v1/sensor/measurements")
    public Observable<AirlyMeasurementsModel> getMeasurementsBySensorId(@Query("sensorId") int sensorId);

}
