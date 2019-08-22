package br.com.analisadorb3.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;
import br.com.analisadorb3.R;
import br.com.analisadorb3.api.ApiConnector;
import br.com.analisadorb3.api.WorldTradingConnector;
import br.com.analisadorb3.models.StockListFragment;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.adaptors.ErrorAdapter;
import br.com.analisadorb3.adaptors.StockAdapter;

public class MainActivity extends AppCompatActivity {

    public static final String SAVED_STOCKS = "SavedStocks";
    public static final String SYMBOL_MESSAGE = "br.com.analisadorb3.SYMBOL";
    public static final String COMPANY_MESSAGE = "br.com.analisadorb3.COMPANY";

    private List<String> symbols;
    private SwipeRefreshLayout refresh;
    private StockListFragment stocks;
    private StockAdapter adapter;
    private ErrorAdapter errorAdapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        symbols = getSymbols();
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

    private List<String> getSymbols(){
        SharedPreferences settings = getSharedPreferences(SAVED_STOCKS, 0);
        try{
            JSONArray  json = new JSONArray(settings.getString("favouriteStocks", ""));
            List<String> list = new ArrayList<>();
            for(int i = 0; i < json.length(); i++){
                list.add(json.get(i).toString());
            }
            return list;
        }
        catch (Exception e){
            return new ArrayList<>();
        }
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
