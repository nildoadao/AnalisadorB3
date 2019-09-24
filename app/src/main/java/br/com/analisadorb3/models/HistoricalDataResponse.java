package br.com.analisadorb3.models;

import java.util.List;
import java.util.Map;

public class HistoricalDataResponse {
    String name;
    List<Map<String, StockHistoricalData>> history;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Map<String, StockHistoricalData>> getHistory() {
        return history;
    }

    public void setHistory(List<Map<String, StockHistoricalData>> history) {
        this.history = history;
    }
}
