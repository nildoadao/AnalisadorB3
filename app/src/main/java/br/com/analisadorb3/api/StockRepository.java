package br.com.analisadorb3.api;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private MutableLiveData<List<StockSearchResult>> searchResults = new MutableLiveData<>();
    private MutableLiveData<List<String>> favouriteStocks = new MutableLiveData<>();
    private MutableLiveData<StockRealTimeData> selectedStock = new MutableLiveData<>();
    private MutableLiveData<Map<String, StockHistoricalData>> dailyData = new MutableLiveData<>();
    private MutableLiveData<Map<String, StockIntraDayData>> intraDayData = new MutableLiveData<>();
    private MutableLiveData<List<StockRealTimeData>> savedStocks = new MutableLiveData<>();

    public MutableLiveData<List<StockSearchResult>> getSearchResults() {
        return searchResults;
    }

    public MutableLiveData<List<String>> getFavouriteStocks() {
        return favouriteStocks;
    }

    public MutableLiveData<StockRealTimeData> getSelectedStock() {
        return selectedStock;
    }

    public MutableLiveData<Map<String, StockHistoricalData>> getDailyData() {
        return dailyData;
    }

    public MutableLiveData<Map<String, StockIntraDayData>> getIntraDayData() {
        return intraDayData;
    }

    public MutableLiveData<List<StockRealTimeData>> getSavedStocks(){
        return savedStocks;
    }

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
        boolean result =  SettingsUtil.removeFavouriteStock(context, symbol);
        favouriteStocks.postValue(SettingsUtil.getFavouriteStocks(context));
        return result;
    }

    public void clearStockHistory(){
        intraDayData.setValue(null);
        dailyData.setValue(null);
    }

    public boolean followStock(Context context, String symbol){
        boolean result = SettingsUtil.saveFavouriteStock(context, symbol);
        favouriteStocks.postValue(SettingsUtil.getFavouriteStocks(context));
        return result;
    }

    public void updateSavedStocks(List<String> symbols){
        updateSavedStocks(TextUtils.join(",", symbols));
    }

    public void updateSelectedStock(String symbol){
        refreshing.setValue(true);
        worldTradingApi.getLastQuote(symbol, WORLD_TRADING_TOKEN)
                .enqueue(new Callback<RealTimeDataResponse>() {
                    @Override
                    public void onResponse(Call<RealTimeDataResponse> call, Response<RealTimeDataResponse> response) {
                        if(!response.isSuccessful()){
                            refreshing.setValue(false);
                            selectedStock.postValue(null);
                            return;
                        }
                        selectedStock.postValue(response.body().getData().get(0));
                        refreshing.setValue(false);
                    }

                    @Override
                    public void onFailure(Call<RealTimeDataResponse> call, Throwable t) {
                        // TODO handle errors
                        selectedStock.postValue(null);
                        refreshing.setValue(false);
                    }
                });
    }

    public void updateSavedStocks(String symbol){
        refreshing.setValue(true);
        worldTradingApi.getLastQuote(symbol, WORLD_TRADING_TOKEN)
                .enqueue(new Callback<RealTimeDataResponse>() {
                    @Override
                    public void onResponse(Call<RealTimeDataResponse> call, Response<RealTimeDataResponse> response) {
                        if(!response.isSuccessful()){
                            refreshing.setValue(false);
                            savedStocks.postValue(null);
                            return;
                        }
                        savedStocks.postValue(response.body().getData());
                        refreshing.setValue(false);
                    }

                    @Override
                    public void onFailure(Call<RealTimeDataResponse> call, Throwable t) {
                        // TODO handle errors
                        savedStocks.postValue(null);
                        refreshing.setValue(false);
                    }
                });
    }

    public void getDailyTimeSeries(String symbol, String dateFrom, String dateTo){
        refreshing.setValue(true);
        worldTradingApi.getDailyTimeSeries(symbol, WORLD_TRADING_TOKEN, "newest", dateFrom, dateTo)
                .enqueue(new Callback<HistoricalDataResponse>() {
                    @Override
                    public void onResponse(Call<HistoricalDataResponse> call, Response<HistoricalDataResponse> response) {
                        if(!response.isSuccessful()){
                            dailyData.postValue(null);
                            return;
                        }
                        dailyData.postValue(response.body().getHistory());
                        refreshing.setValue(false);
                    }

                    @Override
                    public void onFailure(Call<HistoricalDataResponse> call, Throwable t) {
                        dailyData.postValue(null);
                        refreshing.setValue(false);
                        // TODO handle errors
                    }
                });
    }

    public void searchStock(String searchTerm){
        refreshing.setValue(true);
        worldTradingApi.searchStock(searchTerm, WORLD_TRADING_TOKEN)
                .enqueue(new Callback<StockSearchResponse>() {
                    @Override
                    public void onResponse(Call<StockSearchResponse> call, Response<StockSearchResponse> response) {
                        if(!response.isSuccessful()){
                            refreshing.setValue(false);
                            searchResults.postValue(null);
                            return;
                        }
                        searchResults.postValue(response.body().getData());
                        refreshing.setValue(false);
                    }

                    @Override
                    public void onFailure(Call<StockSearchResponse> call, Throwable t) {
                        // TODO handle errors
                        searchResults.postValue(null);
                        refreshing.setValue(false);
                    }
                });
    }

    public void getIntraDayTimeSeries(String symbol){
        alphaVantageAPI.getIntradayData("TIME_SERIES_INTRADAY", "1min",
                symbol, ALPHAVANTAGE_API_KEY, "full")
                .enqueue(new Callback<IntradayDataResponse>(){
                    @Override
                    public void onFailure(Call<IntradayDataResponse> call, Throwable t) {
                        // TODO handle errors
                        intraDayData.postValue(null);
                    }

                    @Override
                    public void onResponse(Call<IntradayDataResponse> call, Response<IntradayDataResponse> response) {
                        if(!response.isSuccessful()){
                            intraDayData.postValue(null);
                            return;
                        }
                        intraDayData.postValue(response.body().getTimeSeries());
                    }
                });
    }
}
