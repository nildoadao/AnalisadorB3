package br.com.analisadorb3.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private static Retrofit basicRetrofit = new Retrofit.Builder()
            .baseUrl("https://api.worldtradingdata.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static Retrofit intradayRetrofit = new Retrofit.Builder()
            .baseUrl("https://www.alphavantage.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static <S> S createService(Class<S> serviceClass){
        return basicRetrofit.create(serviceClass);
    }

    public static <S> S createIntradayService(Class<S> serviceClass){
        return intradayRetrofit.create(serviceClass);
    }
}
