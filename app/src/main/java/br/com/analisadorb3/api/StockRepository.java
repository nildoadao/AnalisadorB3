package br.com.analisadorb3.api;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import br.com.analisadorb3.models.RealTimeDataResponse;
import br.com.analisadorb3.models.StockHistorycalData;
import br.com.analisadorb3.models.StockRealTimeData;
import br.com.analisadorb3.models.StockSearchResponse;
import br.com.analisadorb3.models.StockSearchResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockRepository {

    private final String WORLD_TRADING_TOKEN = "rwNhIENB5s0xpmzAOzBmyBBW944f8uoBUHkT7qEwVsKRXrzFRmLqpFDEo8Eq";

    private static StockRepository stockRepository;
    private WorldTradingApi worldTradingApi;
    private String errorMessage;
    private OnRequestCompletedListener requestListener;
    private MutableLiveData<List<StockSearchResult>> searchResult = new MutableLiveData<>();


    public interface OnRequestCompletedListener{
        void onRequestCompleted();
    }

    public void setOnRequestCompletedListener(OnRequestCompletedListener listener){
        requestListener = listener;
    }

    public static StockRepository getInstance(){
        if(stockRepository == null)
            stockRepository = new StockRepository();

        return stockRepository;
    }

    public StockRepository(){
        worldTradingApi = RetrofitService.createService(WorldTradingApi.class, false);
    }

    public String getErrorMessage(){
        return errorMessage;
    }

    public MutableLiveData<List<StockSearchResult>> getSearchResult(){
        return searchResult;
    }

    private void onRequestCompleted(){
        if(requestListener != null)
            requestListener.onRequestCompleted();
    }

    public MutableLiveData<List<StockRealTimeData>> getLastQuote(List<String> symbols){

        final MutableLiveData<List<StockRealTimeData>> lastQuote = new MutableLiveData<>();
        worldTradingApi.getLastQuote(TextUtils.join(",", symbols), WORLD_TRADING_TOKEN)
                .enqueue(new Callback<RealTimeDataResponse>() {
                    @Override
                    public void onResponse(Call<RealTimeDataResponse> call, Response<RealTimeDataResponse> response) {
                        if(!response.isSuccessful()){
                            errorMessage = "Status code" + response.code();
                            lastQuote.setValue(null);
                            onRequestCompleted();
                            return;
                        }
                        lastQuote.setValue(response.body().getData());
                        onRequestCompleted();
                    }

                    @Override
                    public void onFailure(Call<RealTimeDataResponse> call, Throwable t) {
                        errorMessage = t.getMessage();
                        lastQuote.setValue(null);
                        onRequestCompleted();
                    }
                });
        return lastQuote;
    }

    public MutableLiveData<List<StockHistorycalData>> getDailyTimeSeries(String symbol, String dateFrom, String dateTo){
        final MutableLiveData<List<StockHistorycalData>> dailyData = new MutableLiveData<>();
        worldTradingApi.getDailyTimeSeries(symbol, WORLD_TRADING_TOKEN, dateFrom, dateTo)
                .enqueue(new Callback<List<StockHistorycalData>>() {
                    @Override
                    public void onResponse(Call<List<StockHistorycalData>> call, Response<List<StockHistorycalData>> response) {
                        if(!response.isSuccessful()){
                            dailyData.setValue(null);
                            onRequestCompleted();
                            return;
                        }
                        dailyData.setValue(response.body());
                        onRequestCompleted();
                    }

                    @Override
                    public void onFailure(Call<List<StockHistorycalData>> call, Throwable t) {
                        dailyData.setValue(null);
                        onRequestCompleted();
                    }
                });
        return dailyData;
    }

    public void searchStock(String searchTerm){
        worldTradingApi.searchStock(searchTerm, WORLD_TRADING_TOKEN)
                .enqueue(new Callback<StockSearchResponse>() {
                    @Override
                    public void onResponse(Call<StockSearchResponse> call, Response<StockSearchResponse> response) {
                        if(!response.isSuccessful()){
                            searchResult.postValue(null);
                            onRequestCompleted();
                            return;
                        }
                        searchResult.postValue(response.body().getData());
                        onRequestCompleted();
                    }

                    @Override
                    public void onFailure(Call<StockSearchResponse> call, Throwable t) {
                        searchResult.postValue(null);
                        onRequestCompleted();
                    }
                });
    }
}
