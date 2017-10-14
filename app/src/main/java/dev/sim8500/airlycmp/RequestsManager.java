package dev.sim8500.airlycmp;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

import com.google.gson.GsonBuilder;

import java.io.IOException;

/**
 * Created by sbernad on 27/08/2017.
 */

public class RequestsManager {

    public static RequestsManager getInstance() { return instance; }


    public <T> T getAirlyObservableService(Class<T> service) {

        return prepareAirlyObservableInstance().create(service);
    }

    public <T> T getGiosObservableService(Class<T> service) {

        return prepareGiosObservableInstance().create(service);
    }

    private RequestsManager() { }

    private static final RequestsManager instance = new RequestsManager();
    private static final String BASE_AIRLY_API_URL = "https://airapi.airly.eu";
    private static final String BASE_GIOS_API_URL = "http://api.gios.gov.pl";
    private static final String AIRLY_API_KEY = "7d320318af95410b94fcfa864926deb7";


    private Retrofit prepareAirlyObservableInstance() {

        OkHttpClient httpClient = new OkHttpClient.Builder()
                                    .addInterceptor(new Interceptor()
                                    {
                                        @Override
                                        public Response intercept(Chain chain) throws IOException
                                        {
                                            Request newRequest = chain.request().newBuilder()
                                                    .addHeader("Accept", "application/json")
                                                    .addHeader("apikey", AIRLY_API_KEY).build();

                                            return chain.proceed(newRequest);
                                        }
                                    })
                                    .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_AIRLY_API_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(httpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit;
    }

    private Retrofit prepareGiosObservableInstance() {

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor()
                {
                    @Override
                    public Response intercept(Chain chain) throws IOException
                    {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Accept", "application/json")
                                .build();

                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_GIOS_API_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(httpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit;
    }


}
