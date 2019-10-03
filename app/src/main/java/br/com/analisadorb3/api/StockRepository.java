package br.com.analisadorb3.api;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.analisadorb3.models.HistoricalDataResponse;
import br.com.analisadorb3.models.IntradayDataResponse;
import br.com.analisadorb3.models.RealTimeDataResponse;
import br.com.analisadorb3.models.StockHistoricalData;
import br.com.analisadorb3.models.StockIntraDayData;
import br.com.analisadorb3.models.StockRealTimeData;
import br.com.analisadorb3.models.StockSearchResponse;
import br.com.analisadorb3.models.StockSearchResult;
import br.com.analisadorb3.util.SettingsUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockRepository {

    private final String WORLD_TRADING_TOKEN = "rwNhIENB5s0xpmzAOzBmyBBW944f8uoBUHkT7qEwVsKRXrzFRmLqpFDEo8Eq";
    private final String ALPHAVANTAGE_API_KEY = "XC5MVLREL74KNLOR";

    private static StockRepository stockRepository;
    private WorldTradingApi worldTradingApi;
    private AlphaVantageAPI alphaVantageAPI;
    private MutableLiveData<Boolean> refreshing = new MutableLiveData<>();

    public static StockRepository getInstance(){
        if(stockRepository == null)
            stockRepository = new StockRepository();

        return stockRepository;
    }

    public StockRepository(){
        worldTradingApi = RetrofitService.createService(WorldTradingApi.class);
        alphaVantageAPI = RetrofitService.createIntradayService(AlphaVantageAPI.class);
        refreshing.setValue(false);
    }

    public MutableLiveData<Boolean> isRefreshing(){
        return refreshing;
    }

    public boolean unfollowStock(Context context, String symbol){
        return SettingsUtil.removeFavouriteStock(context, symbol);
    }

    public boolean followStock(Context context, String symbol){
        return SettingsUtil.saveFavouriteStock(context, symbol);
    }

    public List<StockRealTimeData> getLastQuote(List<String> symbols){
        return getLastQuote(TextUtils.join(",", symbols));
    }

    public List<StockRealTimeData> getLastQuote(String symbol){
        refreshing.setValue(true);
        final List<StockRealTimeData> stocks = new ArrayList<>();
        worldTradingApi.getLastQuote(symbol, WORLD_TRADING_TOKEN)
                .enqueue(new Callback<RealTimeDataResponse>() {
                    @Override
                    public void onResponse(Call<RealTimeDataResponse> call, Response<RealTimeDataResponse> response) {
                        if(!response.isSuccessful()){
                            refreshing.setValue(false);
                            return;
                        }
                        for(StockRealTimeData data : response.body().getData()){
                            stocks.add(data);
                        }
                        refreshing.setValue(false);
                    }

                    @Override
                    public void onFailure(Call<RealTimeDataResponse> call, Throwable t) {
                        // TODO handle errors
                        refreshing.setValue(false);
                    }
                });
        return stocks;
    }

    public Map<String, StockHistoricalData> getDailyTimeSeries(String symbol, String dateFrom, String dateTo){
        refreshing.setValue(true);
        final Map<String, StockHistoricalData> history = new HashMap<>();
        worldTradingApi.getDailyTimeSeries(symbol, WORLD_TRADING_TOKEN, "newest", dateFrom, dateTo)
                .enqueue(new Callback<HistoricalDataResponse>() {
                    @Override
                    public void onResponse(Call<HistoricalDataResponse> call, Response<HistoricalDataResponse> response) {
                        if(!response.isSuccessful()){
                            return;
                        }
                        for(String key : response.body().getHistory().keySet()){
                            history.put(key, response.body().getHistory().get(key));
                        }
                    }

                    @Override
                    public void onFailure(Call<HistoricalDataResponse> call, Throwable t) {
                        // TODO handle errors
                    }
                });
        return history;
    }

    public List<StockSearchResult> searchStock(String searchTerm){
        refreshing.setValue(true);
        final List<StockSearchResult> results = new ArrayList<>();
        worldTradingApi.searchStock(searchTerm, WORLD_TRADING_TOKEN)
                .enqueue(new Callback<StockSearchResponse>() {
                    @Override
                    public void onResponse(Call<StockSearchResponse> call, Response<StockSearchResponse> response) {
                        if(!response.isSuccessful()){
                            refreshing.setValue(false);
                            return;
                        }
                        for(StockSearchResult result : response.body().getData()){
                            results.add(result);
                        }
                        refreshing.setValue(false);
                    }

                    @Override
                    public void onFailure(Call<StockSearchResponse> call, Throwable t) {
                        // TODO handle errors
                        refreshing.setValue(false);
                    }
                });
        return results;
    }

    public Map<String, StockIntraDayData> getIntraDayTimeSeries(String symbol){
        refreshing.setValue(true);
        final Map<String, StockIntraDayData> stocks = new HashMap<>();
        alphaVantageAPI.getIntradayData("TIME_SERIES_INTRADAY", "1min",
                symbol, ALPHAVANTAGE_API_KEY, "full")
                .enqueue(new Callback<IntradayDataResponse>(){
                    @Override
                    public void onFailure(Call<IntradayDataResponse> call, Throwable t) {
                        // TODO handle errors
                        refreshing.setValue(false);
                    }

                    @Override
                    public void onResponse(Call<IntradayDataResponse> call, Response<IntradayDataResponse> response) {
                        if(!response.isSuccessful()){
                            refreshing.setValue(false);
                            return;
                        }
                        for(String key : response.body().getTimeSeries().keySet()){
                            stocks.put(key, response.body().getTimeSeries().get(key));
                        }
                        refreshing.setValue(false);
                    }
                });
        return stocks;
    }
}
