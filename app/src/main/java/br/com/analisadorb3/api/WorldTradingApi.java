package br.com.analisadorb3.api;

import java.util.List;

import br.com.analisadorb3.models.RealTimeDataResponse;
import br.com.analisadorb3.models.StockHistorycalData;
import br.com.analisadorb3.models.StockSearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WorldTradingApi {

    @GET("stock")
    Call<RealTimeDataResponse> getLastQuote(@Query("symbols") String symbols,
                                            @Query("api_token") String token);

    @GET("history")
    Call<List<StockHistorycalData>> getDailyTimeSeries(@Query("symbol") String symbol,
                                                       @Query("api_token") String token,
                                                       @Query("date_from") String dateFrom,
                                                       @Query("date_to") String dateTo);

    @GET("stock_search")
    Call<List<StockSearchResult>> searchStock(@Query("search_term") String searchTerm,
                                              @Query("api_token") String token);
}
