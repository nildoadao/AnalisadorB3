package br.com.analisadorb3.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.analisadorb3.R;
import br.com.analisadorb3.api.ApiConnector;
import br.com.analisadorb3.api.ApiException;
import br.com.analisadorb3.api.WorldTradingConnector;
import br.com.analisadorb3.models.StockFragment;
import br.com.analisadorb3.models.StockListFragment;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.adaptors.SearchItemAdapter;
import br.com.analisadorb3.util.SettingsUtil;

public class SearchActivity extends AppCompatActivity {

    public static final String SYMBOL_MESSAGE = "br.com.analisadorb3.search.SYMBOL";
    public static final String COMPANY_MESSAGE = "br.com.analisadorb3.search.COMPANY";
    private SearchItemAdapter searchItemAdapter;
    private StockListFragment stocksFragment;

    ProgressBar searchProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        FragmentManager manager = getSupportFragmentManager();
        stocksFragment = (StockListFragment) manager.findFragmentByTag("stocksFragment");

        if(stocksFragment == null){
            stocksFragment = new StockListFragment();
            stocksFragment.setData(new ArrayList<StockQuote>());
            manager.beginTransaction().add(stocksFragment, "stocksFragment").commit();
        }

        final EditText search = findViewById(R.id.search_input);
        searchProgress = findViewById(R.id.search_progressbar);
        searchItemAdapter = new SearchItemAdapter(this, stocksFragment.getData());
        searchItemAdapter.setOnItemClickListener(new SearchItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Context context, int position) {
                Intent intent = new Intent(context, StockInfoActivity.class);
                intent.putExtra(SYMBOL_MESSAGE, searchItemAdapter.getData().get(position).getSymbol());
                intent.putExtra(COMPANY_MESSAGE, searchItemAdapter.getData().get(position).getCompany());
                startActivity(intent);
            }
        });

        searchItemAdapter.setOnButtonClickListener(new SearchItemAdapter.OnButtonClickListener() {
            @Override
            public void onButtonClick(Context context, int position) {
                SettingsUtil settings = new SettingsUtil(getApplicationContext());
                if(settings.saveFavouriteStock(searchItemAdapter.getData().get(position).getSymbol()))
                    Toast.makeText(getBaseContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getBaseContext(), getString(R.string.stocks_saved_limit), Toast.LENGTH_LONG).show();

                searchItemAdapter.notifyDataSetChanged();
            }
        });

        ListView resultList = findViewById(R.id.search_result_list);
        resultList.setAdapter(searchItemAdapter);
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    stocksFragment.getData().clear();
                    searchItemAdapter.notifyDataSetChanged();
                    new SearchTask().execute(search.getText().toString());
                    searchProgress.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });
        ImageButton backButton = findViewById(R.id.search_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        search.requestFocus();
    }

    public void onDestroy(){
        super.onDestroy();
        stocksFragment.setData(stocksFragment.getData());
    }

    private void onSearchCompleted(List<StockQuote> list){
        if(list != null){
            for(StockQuote stock : list)
                stocksFragment.getData().add(stock);

            searchItemAdapter.notifyDataSetChanged();
            searchProgress.setVisibility(View.INVISIBLE);
        }
    }

    public class SearchTask extends AsyncTask<String, Void, List<StockQuote>> {

        @Override
        protected List<StockQuote> doInBackground(String... params) {
            try{
                ApiConnector api = new WorldTradingConnector(getBaseContext());
                return api.searchEndpoint(params[0]);
            }
            catch (ApiException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<StockQuote> result) {
            super.onPostExecute(result);
            onSearchCompleted(result);
        }
    }
}
