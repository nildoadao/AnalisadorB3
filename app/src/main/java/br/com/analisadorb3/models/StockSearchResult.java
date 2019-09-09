package br.com.analisadorb3.models;

import com.google.gson.annotations.SerializedName;

public class StockSearchResult {
    private String symbol;
    private String name;
    private String currency;
    private String price;
    @SerializedName("stock_exchange_long")
    private String stockExchangeLong;
    @SerializedName("stock_exchange_short")
    private String stockExchangeShort;

}
