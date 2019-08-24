package br.com.analisadorb3.models;

import java.time.LocalDate;

public class StockQuote implements Comparable<Object>{

    private String company;
    private String symbol;
    private String price;
    private LocalDate date;
    private String open;
    private String high;
    private String low;
    private String close;
    private String volume;
    private LocalDate lastTradingDay;
    private String previousClose;
    private String change;
    private String changePercent;
    private String marketCapital;
    private String currency;


    public String getCompany(){return company; }
    public void setCompany(String company) { this.company = company; }
    public String getSymbol(){
        return symbol;
    }
    public void setSymbol(String symbol){
        this.symbol = symbol;
    }
    public String getPrice(){ return price; }
    public void setPrice(String price){
        this.price = price;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getOpen() { return open; }
    public void setOpen(String open) {
        this.open = open;
    }
    public String getHigh() {
        return high;
    }
    public void setHigh(String high) {
        this.high = high;
    }
    public String getLow() {
        return low;
    }
    public void setLow(String low) {
        this.low = low;
    }
    public String getClose() {
        return close;
    }
    public void setClose(String close) {
        this.close = close;
    }
    public String getVolume() {
        return volume;
    }
    public void setVolume(String volume) {
        this.volume = volume;
    }
    public LocalDate getLastTradingDay() {
        return lastTradingDay;
    }
    public void setLastTradingDay(LocalDate lastTradingDay) { this.lastTradingDay = lastTradingDay; }
    public String getPreviousClose() {
        return previousClose;
    }
    public void setPreviousClose(String previousClose) {
        this.previousClose = previousClose;
    }
    public String getChange() {
        return change;
    }
    public void setChange(String change) {
        this.change = change;
    }
    public String getChangePercent() {
        return changePercent;
    }
    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }
    public String getMarketCapital() { return marketCapital; }
    public void setMarketCapital(String marketCapital) { this.marketCapital = marketCapital; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    @Override
    public int compareTo(Object arg0) {
        StockQuote s = (StockQuote)arg0;
        return this.getDate().compareTo(s.getDate());
    }
}