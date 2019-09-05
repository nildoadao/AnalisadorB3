package br.com.analisadorb3.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import br.com.analisadorb3.R;
import br.com.analisadorb3.api.AlphaVantageConnector;
import br.com.analisadorb3.api.ApiConnector;
import br.com.analisadorb3.api.ApiException;
import br.com.analisadorb3.api.WorldTradingConnector;
import br.com.analisadorb3.fragments.StockFragment;
import br.com.analisadorb3.fragments.StockListFragment;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.adapters.ErrorAdapter;
import br.com.analisadorb3.adapters.StockInfoAdapter;

public class StockInfoActivity extends AppCompatActivity {

    private StockFragment lastQuoteFragment;
    private StockListFragment intraDayDataFragment;
    private StockListFragment dailyDataFragment;
    private StockInfoAdapter stockInfoAdapter;
    private String symbol;
    private String company;

    private String getSymbol(){
        return symbol;
    }

    private void setSymbol(String symbol){
        this.symbol = symbol;
    }

    private String getCompany(){
        return company;
    }

    private void setCompany(String company){
        this.company = company;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_info);
        getExtras();
        initStockQuoteFragments();
        initCompanyTextView();
        initStockInfoAdapter();
        initBackButton();
        initRefreshLayout();
    }

    private void initRefreshLayout(){
        final SwipeRefreshLayout refreshLayout = findViewById(R.id.stock_info_swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!refreshLayout.isRefreshing())
                    refreshLayout.setRefreshing(true);

                new LoadStockData().execute(getSymbol());
            }
        });
    }

    private void initBackButton(){
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initStockInfoAdapter(){
        stockInfoAdapter = new StockInfoAdapter(this, getSupportFragmentManager());
        stockInfoAdapter.setLastQuote(lastQuoteFragment.getData());
        stockInfoAdapter.setIntraDayData(intraDayDataFragment.getData());
        stockInfoAdapter.setDailyData(dailyDataFragment.getData());
        ListView stockList = findViewById(R.id.stock_info_list);
        stockList.setAdapter(stockInfoAdapter);
    }

    private void initCompanyTextView(){
        TextView companyName = findViewById(R.id.stock_info_company);
        companyName.setText(getCompany());
    }

    private void initStockQuoteFragments(){
        FragmentManager manager = getSupportFragmentManager();
        lastQuoteFragment = (StockFragment) manager.findFragmentByTag("lastQuote");
        intraDayDataFragment = (StockListFragment) manager.findFragmentByTag("intraDayData");
        dailyDataFragment = (StockListFragment) manager.findFragmentByTag("dailyData");

        if(lastQuoteFragment == null || dailyDataFragment == null){
            lastQuoteFragment = new StockFragment();
            lastQuoteFragment.setData(new StockQuote());
            intraDayDataFragment = new StockListFragment();
            intraDayDataFragment.setData(new ArrayList<StockQuote>());
            dailyDataFragment = new StockListFragment();
            dailyDataFragment.setData(new ArrayList<StockQuote>());
            manager.beginTransaction().add(lastQuoteFragment, "lastQuote").commit();
            manager.beginTransaction().add(intraDayDataFragment, "intraDayData").commit();
            manager.beginTransaction().add(dailyDataFragment, "dailyData").commit();
            SwipeRefreshLayout refreshLayout = findViewById(R.id.stock_info_swipe_refresh);
            refreshLayout.setRefreshing(true);
            new LoadStockData().execute(getSymbol());
        }
    }

    private void getExtras(){
        Intent intent = getIntent();
        //setSymbol(intent.getStringExtra(MainActivity.SYMBOL_MESSAGE));
        //setCompany(intent.getStringExtra(MainActivity.COMPANY_MESSAGE));

        if(symbol == null){ // Coming from search
            setSymbol(intent.getStringExtra(SearchActivity.SYMBOL_MESSAGE));
            setCompany(intent.getStringExtra(SearchActivity.COMPANY_MESSAGE));
        }
    }

    public void onDestroy(){
        super.onDestroy();
        lastQuoteFragment.setData(lastQuoteFragment.getData());
        intraDayDataFragment.setData(intraDayDataFragment.getData());
        dailyDataFragment.setData(dailyDataFragment.getData());
    }

    private void setErrorAdapter(String message){
        ErrorAdapter errorAdapter = new ErrorAdapter(this, message);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.stock_info_swipe_refresh);
        ListView stockList = findViewById(R.id.stock_info_list);
        stockList.setAdapter(errorAdapter);
        refreshLayout.setRefreshing(false);
    }

    private void onLoadStockDataCompleted(StockQuote lastQuote, List<StockQuote> daily, List<StockQuote> intraDay){

        SwipeRefreshLayout refreshLayout = findViewById(R.id.stock_info_swipe_refresh);

        if(intraDay != null){
            intraDayDataFragment.getData().clear();

            for(StockQuote quote : intraDay)
                intraDayDataFragment.getData().add(quote);
        }

        dailyDataFragment.getData().clear();

        for(StockQuote quote : daily)
            dailyDataFragment.getData().add(quote);

        lastQuoteFragment.setData(lastQuote);
        stockInfoAdapter = new StockInfoAdapter(this, getSupportFragmentManager());
        stockInfoAdapter.setLastQuote(lastQuote);
        stockInfoAdapter.setIntraDayData(intraDay);
        stockInfoAdapter.setDailyData(daily);
        ListView stockList = findViewById(R.id.stock_info_list);
        stockList.setAdapter(stockInfoAdapter);
        refreshLayout.setRefreshing(false);
    }

    public class LoadStockData extends AsyncTask<String, Void, Void>{

        private StockQuote lastQuote;
        private List<StockQuote> dailyData;
        private List<StockQuote> intraDayData;
        private String errorMessage;

        private String getErrorMessage(){
            return errorMessage;
        }

        private void setErrorMessage(String message){
            errorMessage = message;
        }
        @Override
        protected Void doInBackground(String... params) {
            try{
                ApiConnector api = new WorldTradingConnector(getBaseContext());
                lastQuote = api.getLastQuote(params[0]);
                dailyData = api.getDailyTimeSeries(params[0]);
            }
            catch (ApiException e){
                setErrorMessage(e.getMessage());
                lastQuote = null;
                dailyData = null;
            }
            try{
                ApiConnector api = new AlphaVantageConnector(getBaseContext());
                intraDayData = api.getIntradayTimeSeries(params[0]);
            }
            catch (ApiException e){
                intraDayData = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(dailyData == null || lastQuote == null)
                setErrorAdapter(getErrorMessage());
            else
                onLoadStockDataCompleted(lastQuote, dailyData, intraDayData);
        }
    }
}
