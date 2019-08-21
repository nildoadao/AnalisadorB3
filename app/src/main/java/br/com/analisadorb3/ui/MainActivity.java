package br.com.analisadorb3.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import br.com.analisadorb3.R;
import br.com.analisadorb3.api.ApiConnector;
import br.com.analisadorb3.api.WorldTradingConnector;
import br.com.analisadorb3.models.StockListFragment;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.adaptors.ErrorAdapter;
import br.com.analisadorb3.adaptors.StockAdapter;

public class MainActivity extends AppCompatActivity {

    public static final String SYMBOL_MESSAGE = "br.com.analisadorb3.SYMBOL";
    public static final String COMPANY_MESSAGE = "br.com.analisadorb3.COMPANY";
    public static final String PRICE_MESSAGE = "br.com.analisadorb3.PRICE";
    public static final String CURRENCY_MESSAGE = "br.com.analisadorb3.CURRENCY";
    public static final String CHANGE_MESSAGE = "br.com.analisadorb3.CHANGE";
    public static final String CHANGE_PERCENT_MESSAGE = "br.com.analisadorb3.CHANGE_PERCENT";
    public static final String OPEN_MESSAGE = "br.com.analisadorb3.OPEN";
    public static final String PREVIOUS_CLOSE_MESSAGE = "br.com.analisadorb3.PREVIOUS_CLOSE";
    public static final String VOLUME_MESSAGE = "br.com.analisadorb3.VOLUME";
    public static final String DAY_HIGH_MESSAGE = "br.com.analisadorb3.DAY_HIGH";
    public static final String DAY_LOW_MESSAGE = "br.com.analisadorb3.DAY_LOW";

    private List<String> symbols = Arrays.asList(
            "B3SA3.SA", "VVAR3.SA", "ITSA4.SA");

    private SwipeRefreshLayout refresh;
    private StockListFragment stocks;
    private StockAdapter adapter;
    private ErrorAdapter errorAdapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager manager = getSupportFragmentManager();
        stocks = (StockListFragment) manager.findFragmentByTag("stockListFragment");

        if(stocks == null){
            stocks = new StockListFragment();
            stocks.setData(new ArrayList<StockQuote>());
            manager.beginTransaction().add(stocks, "stockListFragment").commit();
            refresh = findViewById(R.id.pullToRefresh);
            refresh.setRefreshing(true);
            new LoadStockTask().execute(symbols);
        }

        list = findViewById(R.id.stock_list);
        adapter = new StockAdapter(this, stocks.getData());
        adapter.setOnItemClickListener(new StockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Context context, int position) {
                Intent intent = new Intent(context, StockInfoActivity.class);
                intent.putExtra(SYMBOL_MESSAGE, stocks.getData().get(position).getSymbol());
                intent.putExtra(COMPANY_MESSAGE, stocks.getData().get(position).getCompany());
                intent.putExtra(PRICE_MESSAGE, stocks.getData().get(position).getPrice());
                intent.putExtra(CURRENCY_MESSAGE, stocks.getData().get(position).getCurrency());
                intent.putExtra(CHANGE_PERCENT_MESSAGE, stocks.getData().get(position).getChangePercent());
                intent.putExtra(CHANGE_MESSAGE, stocks.getData().get(position).getChange());
                intent.putExtra(OPEN_MESSAGE, stocks.getData().get(position).getOpen());
                intent.putExtra(PREVIOUS_CLOSE_MESSAGE, stocks.getData().get(position).getPreviousClose());
                intent.putExtra(VOLUME_MESSAGE, stocks.getData().get(position).getVolume());
                intent.putExtra(DAY_HIGH_MESSAGE, stocks.getData().get(position).getHigh());
                intent.putExtra(DAY_LOW_MESSAGE, stocks.getData().get(position).getLow());
                startActivity(intent);
            }
        });
        ImageButton search = findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        list.setAdapter(adapter);
        refresh = findViewById(R.id.pullToRefresh);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!refresh.isRefreshing())
                    refresh.setRefreshing(true);

                new LoadStockTask().execute(symbols);
            }
        });
    }

    public void onDestroy(){
        super.onDestroy();
        stocks.setData(stocks.getData());
    }

    private void onRefreshComplete(List<StockQuote> list){

        if(list == null){
            errorAdapter = new ErrorAdapter(this, getString(R.string.no_internet));
            ListView listView = findViewById(R.id.stock_list);
            listView.setAdapter(errorAdapter);
            refresh.setRefreshing(false);
        }
        else {
            ListView listView = findViewById(R.id.stock_list);
            listView.setAdapter(adapter);
            stocks.getData().clear();

            for(StockQuote stock : list)
                stocks.getData().add(stock);

            adapter.notifyDataSetChanged();
            refresh.setRefreshing(false);
        }
    }

    public class LoadStockTask extends AsyncTask<List<String>, Void, List<StockQuote>> {
        @Override
        protected List<StockQuote> doInBackground(List<String>... params) {
            List<StockQuote> list = new ArrayList<>();
            try {
                ApiConnector api = new WorldTradingConnector(getBaseContext());
                list = api.getLastQuote(params[0]);
            } catch (Exception e) {
                return null;
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<StockQuote> result) {
            super.onPostExecute(result);
            onRefreshComplete(result);
        }
    }
}
