package br.com.analisadorb3.api;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.analisadorb3.models.YahooStockData;
import br.com.analisadorb3.util.SettingsUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockRepository {

    private static StockRepository stockRepository;
    private YahooFinanceApi yahooFinanceApi;
    private MutableLiveData<Boolean> refreshing = new MutableLiveData<>();
    private MutableLiveData<List<String>> favouriteStocks = new MutableLiveData<>();
    private MutableLiveData<YahooStockData> selectedStock = new MutableLiveData<>();
    private MutableLiveData<YahooStockData> dailyData = new MutableLiveData<>();
    private MutableLiveData<YahooStockData> intraDayData = new MutableLiveData<>();
    private MutableLiveData<List<YahooStockData>> savedStocks = new MutableLiveData<>();
    private MutableLiveData<YahooStockData> searchResult = new MutableLiveData<>();

    public MutableLiveData<List<String>> getFavouriteStocks() {
        return favouriteStocks;
    }

    public MutableLiveData<YahooStockData> getSelectedStock() {
        return selectedStock;
    }

    public MutableLiveData<YahooStockData> getDailyData() {
        return dailyData;
    }

    public MutableLiveData<YahooStockData> getIntraDayData() {
        return intraDayData;
    }

    public MutableLiveData<List<YahooStockData>> getSavedStocks(){
        return savedStocks;
    }

    public MutableLiveData<YahooStockData> getSearchResult(){
        return searchResult;
    }

    public static StockRepository getInstance(){
        if(stockRepository == null)
            stockRepository = new StockRepository();

        return stockRepository;
    }

    public StockRepository(){
        yahooFinanceApi = RetrofitService.createService(YahooFinanceApi.class);
        savedStocks.setValue(new ArrayList<YahooStockData>());
        intraDayData.setValue(new YahooStockData());
        dailyData.setValue(new YahooStockData());
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
        refreshing.postValue(true);
        final List<YahooStockData> updatedStocks = new ArrayList<>();
        if(symbols != null && symbols.size() > 0){

            for(String symbol : symbols){
                yahooFinanceApi.getStock(symbol, "5m", "1d")
                        .enqueue(new Callback<YahooStockData>() {
                            @Override
                            public void onResponse(Call<YahooStockData> call, Response<YahooStockData> response) {
                                if(!response.isSuccessful()){
                                    refreshing.postValue(false);
                                    return;
                                }
                                updatedStocks.add(response.body());
                                savedStocks.postValue(updatedStocks);
                                refreshing.postValue(false);
                            }

                            @Override
                            public void onFailure(Call<YahooStockData> call, Throwable t) {
                                refreshing.postValue(false);
                                // TODO handle errors
                            }
                        });
            }
        }
        else {
            savedStocks.postValue(null);
            refreshing.postValue(false);
        }
    }

    public void updateSelectedStock(String symbol, String interval, String range){
        refreshing.postValue(true);
        yahooFinanceApi.getStock(symbol, interval, range)
                .enqueue(new Callback<YahooStockData>() {
                    @Override
                    public void onResponse(Call<YahooStockData> call, Response<YahooStockData> response) {
                        if(!response.isSuccessful()){
                            selectedStock.postValue(null);
                            refreshing.postValue(false);
                            return;
                        }
                        selectedStock.postValue(response.body());
                        refreshing.postValue(false);
                    }

                    @Override
                    public void onFailure(Call<YahooStockData> call, Throwable t) {
                        refreshing.postValue(false);
                        // TODO handle errors
                    }
                });
    }

    public void getDailyTimeSeries(String symbol, String days){
        refreshing.postValue(true);
        yahooFinanceApi.getStock(symbol, "1d    ", days)
                .enqueue(new Callback<YahooStockData>() {
                    @Override
                    public void onResponse(Call<YahooStockData> call, Response<YahooStockData> response) {
                        if(!response.isSuccessful()){
                            dailyData.postValue(null);
                            refreshing.postValue(false);
                            return;
                        }
                        dailyData.postValue(response.body());
                        refreshing.postValue(false);
                    }

                    @Override
                    public void onFailure(Call<YahooStockData> call, Throwable t) {
                        // TODO handle errors
                        dailyData.postValue(null);
                        refreshing.postValue(false);
                    }
                });
    }

    public void getIntraDayTimeSeries(String symbol){
        refreshing.postValue(true);
        yahooFinanceApi.getStock(symbol, "1m", "1d")
                .enqueue(new Callback<YahooStockData>() {
                    @Override
                    public void onResponse(Call<YahooStockData> call, Response<YahooStockData> response) {
                        if(!response.isSuccessful()){
                            intraDayData.postValue(null);
                            refreshing.postValue(false);
                            return;
                        }
                        intraDayData.postValue(response.body());
                        refreshing.postValue(false);
                    }

                    @Override
                    public void onFailure(Call<YahooStockData> call, Throwable t) {
                        // TODO handle errors
                        intraDayData.postValue(null);
                        refreshing.postValue(false);
                    }
                });
    }

    public void searchStockBySymbol(String symbol){
        refreshing.postValue(true);
        yahooFinanceApi.getStock(symbol, "1d", "15m")
                .enqueue(new Callback<YahooStockData>() {
                    @Override
                    public void onResponse(Call<YahooStockData> call, Response<YahooStockData> response) {
                        if(!response.isSuccessful()){
                            searchResult.postValue(null);
                            refreshing.postValue(false);
                            return;
                        }
                        searchResult.postValue(response.body());
                        refreshing.postValue(false);
                    }

                    @Override
                    public void onFailure(Call<YahooStockData> call, Throwable t) {
                        refreshing.postValue(false);
                        // TODO handle errors
                    }
                });
    }
}
