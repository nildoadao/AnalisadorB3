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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStockExchangeLong() {
        return stockExchangeLong;
    }

    public void setStockExchangeLong(String stockExchangeLong) {
        this.stockExchangeLong = stockExchangeLong;
    }

    public String getStockExchangeShort() {
        return stockExchangeShort;
    }

    public void setStockExchangeShort(String stockExchangeShort) {
        this.stockExchangeShort = stockExchangeShort;
    }
}
