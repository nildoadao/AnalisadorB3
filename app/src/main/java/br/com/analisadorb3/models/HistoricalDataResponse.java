package br.com.analisadorb3.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class HistoricalDataResponse {
    String name;

    @SerializedName("history")
    Map<String, StockHistoricalData> history;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, StockHistoricalData> getHistory() {
        return history;
    }

    public void setHistory(Map<String, StockHistoricalData> history) {
        this.history = history;
    }
}
