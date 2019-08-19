package br.com.analisadorb3.models;

import java.time.LocalDate;

public class StockQuote implements Comparable<Object>{

    private String company;
    private String symbol;
    private double price;
    private LocalDate date;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
    private LocalDate lastTradingDay;
    private double previousClose;
    private double change;
    private String changePercent;
    private double marketCapital;
    private String currency;


    public String getCompany(){return company; }
    public void setCompany(String company) { this.company = company; }
    public String getSymbol(){
        return symbol;
    }
    public void setSymbol(String symbol){
        this.symbol = symbol;
    }
    public double getPrice(){ return price; }
    public void setPrice(double price){
        this.price = price;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public double getOpen() {
        return open;
    }
    public void setOpen(double open) {
        this.open = open;
    }
    public double getHigh() {
        return high;
    }
    public void setHigh(double high) {
        this.high = high;
    }
    public double getLow() {
        return low;
    }
    public void setLow(double low) {
        this.low = low;
    }
    public double getClose() {
        return close;
    }
    public void setClose(double close) {
        this.close = close;
    }
    public double getVolume() {
        return volume;
    }
    public void setVolume(double volume) {
        this.volume = volume;
    }
    public LocalDate getLastTradingDay() {
        return lastTradingDay;
    }
    public void setLastTradingDay(LocalDate lastTradingDay) { this.lastTradingDay = lastTradingDay; }
    public double getPreviousClose() {
        return previousClose;
    }
    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
    }
    public double getChange() {
        return change;
    }
    public void setChange(double change) {
        this.change = change;
    }
    public String getChangePercent() {
        return changePercent;
    }
    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }
    public double getMarketCapital() { return marketCapital; }
    public void setMarketCapital(double marketCapital) { this.marketCapital = marketCapital; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    @Override
    public int compareTo(Object arg0) {
        StockQuote s = (StockQuote)arg0;
        return this.getDate().compareTo(s.getDate());
    }
}