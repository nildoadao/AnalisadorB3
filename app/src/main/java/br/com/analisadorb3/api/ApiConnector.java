package br.com.analisadorb3.api;

import java.util.List;

import br.com.analisadorb3.models.StockQuote;

public interface ApiConnector {
    StockQuote getLastQuote(String symbol) throws ApiException;
    List<StockQuote> getIntradayTimeSeries(String symbol) throws ApiException;
    List<StockQuote> getDailyTimeSeries(String symbol) throws ApiException;
    List<StockQuote> getMonthTimeSeries(String symbol) throws ApiException;
    List<StockQuote> getLastQuote(List<String> symbols) throws ApiException;
}
