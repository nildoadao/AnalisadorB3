package br.com.analisadorb3.viewmodel;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import br.com.analisadorb3.R;
import br.com.analisadorb3.api.StockRepository;
import br.com.analisadorb3.models.YahooStockData;
import br.com.analisadorb3.util.SettingsUtil;


public class StockViewModel extends AndroidViewModel {

    private StockRepository repository;
    private ObservableField<String> stockSymbol = new ObservableField<>();
    private ObservableField<String> stockCompany = new ObservableField<>();
    private ObservableField<String> stockPrice = new ObservableField<>();
    private ObservableField<String> stockChangePercent = new ObservableField<>();
    private ObservableInt changeTextColor = new ObservableInt();
    private ObservableField<String> open = new ObservableField<>();
    private ObservableField<String> lastClose = new ObservableField<>();
    private ObservableField<String> low = new ObservableField<>();
    private ObservableField<String> high = new ObservableField<>();
    private ObservableField<String> volume = new ObservableField<>();
    private ObservableField<String> threeMonthsAverageChange = new ObservableField<>();
    private ObservableField<Drawable> followButtonImage = new ObservableField<>();
    private MutableLiveData<List<YahooStockData>> searchResults = new MutableLiveData<>();

    public StockViewModel(@NonNull Application application) {
        super(application);
        repository = StockRepository.getInstance();
    }

    public MutableLiveData<Boolean> isSearching(){
        return repository.isRefreshing();
    }

    public MutableLiveData<YahooStockData> getIntraDayData(){
        return repository.getIntraDayData();
    }

    public MutableLiveData<YahooStockData> getDailyData() {
        return repository.getDailyData();
    }

    public MutableLiveData<List<String>> getFavouriteStocks(){
        return repository.getFavouriteStocks();
    }

    public MutableLiveData<YahooStockData> getSelectedStock(){
        return repository.getSelectedStock();
    }

    public ObservableField<String> getLastClose() {
        return lastClose;
    }

    public ObservableField<String> getOpen() {
        return open;
    }

    public ObservableField<String> getLow() {
        return low;
    }

    public ObservableField<String> getHigh() {
        return high;
    }

    public ObservableField<String> getVolume(){
        return volume;
    }

    public ObservableField<String> getThreeMonthsAverageChange() {
        return threeMonthsAverageChange;
    }

    public ObservableField<String> getStockSymbol() {
        return stockSymbol;
    }

    public ObservableField<String> getStockCompany() {
        return stockCompany;
    }

    public ObservableField<String> getStockPrice() {
        return stockPrice;
    }

    public MutableLiveData<List<YahooStockData>> getSearchResults() {
        return searchResults;
    }

    public ObservableField<String> getStockChangePercent(){
        return stockChangePercent;
    }

    public ObservableInt getChangeTextColor(){
        return changeTextColor;
    }

    public ObservableField<Drawable> getFollowButtonImage(){
        return followButtonImage;
    }

    public boolean followStock(Context context, String symbol){
        return repository.followStock(context, symbol);
    }

    public Drawable getStockFollowStatusImage(){
        String selectedSymbol = SettingsUtil.getSelectedSymbol(getApplication());

        if(SettingsUtil.getFavouriteStocks(getApplication()).contains(selectedSymbol))
            return getApplication().getApplicationContext().getDrawable(R.drawable.star_icon);
        else
            return getApplication().getApplicationContext().getDrawable(R.drawable.plus_icon);
    }

    public boolean unfollowStock(Context context, String symbol){
        return repository.unfollowStock(context, symbol);
    }

    public void search(String searchTerm){}

    public void setSelectedStock(String symbol){
        repository.clearStockHistory();
        SettingsUtil.setSelectedSymbol(getApplication(), symbol);
        repository.updateSelectedStock(symbol, "1m", "1d");
    }

    public void updateView(){
        if(getSelectedStock().getValue() != null){
            stockSymbol.set(getSelectedStock().getValue().getChart().getResult().get(0).getMeta().getSymbol());
            stockCompany.set(getSelectedStock().getValue().getChart().getResult().get(0).getMeta().getExchangeName());
            stockPrice.set(getPrice());
            stockChangePercent.set(getChangeText());
            changeTextColor.set(calculateChangeTextColor());
            lastClose.set(getSelectedStock().getValue().getChart().getResult().get(0).getMeta().getPreviousClose());
            open.set(getSelectedStock().getValue().getChart().getResult().get(0).getMeta().getRegularMarketPrice());
            low.set(String.format("%.2f", getSelectedStock().getValue().getChart().getResult().get(0).getIndicators().getQuote().get(0).getLow().get(0)));
            high.set(String.format("%.2f",  getSelectedStock().getValue().getChart().getResult().get(0).getIndicators().getQuote().get(0).getHigh().get(0)));
            volume.set(getVolumeText());
            threeMonthsAverageChange.set(getThreeMonthsVariation());
            followButtonImage.set(getStockFollowStatusImage());
        }
    }

    public void fetchData(){
        String selectedSymbol = SettingsUtil.getSelectedSymbol(getApplication());
        repository.updateSelectedStock(selectedSymbol, "1m", "1d");
        repository.getDailyTimeSeries(selectedSymbol, "6mo");
        repository.getIntraDayTimeSeries(selectedSymbol);
    }

    public String getPrice(){
        YahooStockData selectedStock = getSelectedStock().getValue();
        if(selectedStock != null){
            String price = selectedStock.getChart().getResult().get(0).getMeta().getRegularMarketPrice();
            String currency = selectedStock.getChart().getResult().get(0).getMeta().getCurrency();
            return String.format("%s %s", price, currency);
        }
        return "";
    }

    private String getVolumeText(){
        Double volume;
        try {
            volume = Double.parseDouble(getSelectedStock().getValue().getChart().getResult()
                    .get(0).getIndicators().getQuote().get(0).getVolume().get(0).toString());
        }
        catch (Exception e){
            volume = 0d;
        }
        return volume.toString();
    }

    public String getChangeText(){
        YahooStockData selectedStock = getSelectedStock().getValue();
        if(selectedStock != null){
            Double dayChange = Double.parseDouble(getSelectedStock().getValue().getChart().getResult().get(0).getMeta().getRegularMarketPrice())
                    - Double.parseDouble(getSelectedStock().getValue().getChart().getResult().get(0).getMeta().getPreviousClose());
            Double dayPercentChange =  dayChange /
                    Double.parseDouble(getSelectedStock().getValue().getChart().getResult().get(0).getMeta().getRegularMarketPrice()) * 100;
            return String.format("%.2f (%.2f", dayChange, dayPercentChange)
                    + "%)";
        }
        return "";
    }

    public int calculateChangeTextColor(){

        Double dayChange;
        try {
            dayChange = Double.parseDouble(getSelectedStock().getValue().getChart().getResult().get(0).getMeta().getRegularMarketPrice())
            - Double.parseDouble(getSelectedStock().getValue().getChart().getResult().get(0).getMeta().getPreviousClose());
        }
        catch (Exception e){
            dayChange = 0d;
        }

        if(dayChange >= 0)
            return Color.argb(255, 0, 127, 0);
        else
            return Color.RED;
    }

    public String getThreeMonthsVariation(){
        Double averageChange = 0d;
        return String.format("%.2f", averageChange);
    }
}
