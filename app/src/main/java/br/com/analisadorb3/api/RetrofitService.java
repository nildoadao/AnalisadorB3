package br.com.analisadorb3.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private static Retrofit basicRetrofit = new Retrofit.Builder()
            .baseUrl("https://api.worldtradingdata.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static <S> S createService(Class<S> serviceClass, boolean intraDay){
        if(intraDay)
            return null;
        else
            return basicRetrofit.create(serviceClass);
    }
}
