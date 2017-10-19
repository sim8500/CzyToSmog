package dev.sim8500.airlycmp;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by sbernad on 16/09/2017.
 */

public interface GiosDataService {

    @GET("/pjp-api/rest/aqindex/getIndex/{station}")
    public Observable<GiosQualityIndexModel> getQualityIndexDataFor(@Path("station") int stationId);

    @GET("/pjp-api/rest/station/sensors/{station}")
    public Observable<List<GiosSensorInfoModel>> getSensorsInfo(@Path("station") int stationId);

    @GET("/pjp-api/rest/data/getData/{sensor}")
    public Observable<GiosSensorDataModel> getSensorData(@Path("sensor") int sensorId);

    @GET("/pjp-api/rest/station/findAll")
    public Observable<List<GiosStationDataModel>> getStations();
}
