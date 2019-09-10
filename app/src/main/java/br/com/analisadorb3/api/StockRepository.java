package br.com.analisadorb3.api;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import br.com.analisadorb3.models.RealTimeDataResponse;
import br.com.analisadorb3.models.StockHistorycalData;
import br.com.analisadorb3.models.StockRealTimeData;
import br.com.analisadorb3.models.StockSearchResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockRepository {

    private final String WORLD_TRADING_TOKEN = "rwNhIENB5s0xpmzAOzBmyBBW944f8uoBUHkT7qEwVsKRXrzFRmLqpFDEo8Eq";
    private static StockRepository stockRepository;
    private WorldTradingApi worldTradingApi;

    public static StockRepository getInstance(){
        if(stockRepository == null)
            stockRepository = new StockRepository();

        return stockRepository;
    }

    public StockRepository(){
        worldTradingApi = RetrofitService.createService(WorldTradingApi.class, false);
    }

    public MutableLiveData<List<StockRealTimeData>> getLastQuote(List<String> symbols){

        final MutableLiveData<List<StockRealTimeData>> lastQuote = new MutableLiveData<>();
        worldTradingApi.getLastQuote(String.join(",", symbols), WORLD_TRADING_TOKEN)
                .enqueue(new Callback<RealTimeDataResponse>() {
                    @Override
                    public void onResponse(Call<RealTimeDataResponse> call, Response<RealTimeDataResponse> response) {
                        if(!response.isSuccessful()){
                            lastQuote.setValue(null);
                            return;
                        }
                        lastQuote.setValue(response.body().getData());
                    }

                    @Override
                    public void onFailure(Call<RealTimeDataResponse> call, Throwable t) {
                        lastQuote.setValue(null);
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
                            return;
                        }
                        dailyData.setValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<StockHistorycalData>> call, Throwable t) {
                        dailyData.setValue(null);
                    }
                });
        return dailyData;
    }

    public MutableLiveData<List<StockSearchResult>> searchStock(String searchTerm){
        final MutableLiveData<List<StockSearchResult>> searchResult = new MutableLiveData<>();
        worldTradingApi.searchStock(searchTerm, WORLD_TRADING_TOKEN)
                .enqueue(new Callback<List<StockSearchResult>>() {
                    @Override
                    public void onResponse(Call<List<StockSearchResult>> call, Response<List<StockSearchResult>> response) {
                        if(!response.isSuccessful()){
                            searchResult.setValue(null);
                            return;
                        }
                        searchResult.setValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<StockSearchResult>> call, Throwable t) {
                        searchResult.setValue(null);
                    }
                });
        return searchResult;
    }
}
