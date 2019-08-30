package br.com.analisadorb3.ui;

import androidx.appcompat.app.AppCompatActivity;
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
import br.com.analisadorb3.adaptors.EmptySearchAdapter;
import br.com.analisadorb3.adaptors.ErrorAdapter;
import br.com.analisadorb3.api.ApiConnector;
import br.com.analisadorb3.api.ApiException;
import br.com.analisadorb3.api.WorldTradingConnector;
import br.com.analisadorb3.fragments.StockListFragment;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.adaptors.SearchItemAdapter;
import br.com.analisadorb3.util.SettingsUtil;

public class SearchActivity extends AppCompatActivity {

    //Extras to pass to Stock Info Activity
    public static final String SYMBOL_MESSAGE = "br.com.analisadorb3.search.SYMBOL";
    public static final String COMPANY_MESSAGE = "br.com.analisadorb3.search.COMPANY";

    private SearchItemAdapter searchItemAdapter;
    private StockListFragment stocksFragment;
    private ProgressBar searchProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initStockFragment();
        initSearchItemAdapter();
        initResultList();
        initBackButton();
        initSearchTextInput();
    }

    private void initBackButton(){
        ImageButton backButton = findViewById(R.id.search_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initResultList(){
        ListView resultList = findViewById(R.id.search_result_list);
        resultList.setAdapter(searchItemAdapter);
    }

    private void initSearchTextInput(){
        final EditText search = findViewById(R.id.search_input);
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
        search.requestFocus();
    }

    private void initSearchItemAdapter(){
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
    }

    private void initStockFragment(){
        stocksFragment = (StockListFragment)getSupportFragmentManager().findFragmentByTag("stocksFragment");

        if(stocksFragment == null){
            stocksFragment = new StockListFragment();
            stocksFragment.setData(new ArrayList<StockQuote>());
            getSupportFragmentManager().beginTransaction().add(stocksFragment, "stocksFragment").commit();
        }
    }

    public void onDestroy(){
        super.onDestroy();
        stocksFragment.setData(stocksFragment.getData());
    }

    private void setErrorAdapter(String message){
        ListView resultList = findViewById(R.id.search_result_list);
        ErrorAdapter errorAdapter = new ErrorAdapter(this, message);
        resultList.setAdapter(errorAdapter);
        searchProgress.setVisibility(View.INVISIBLE);
    }

    private void setEmptySearchAdapter(){
        ListView resultList = findViewById(R.id.search_result_list);
        EmptySearchAdapter emptySearchAdapter = new EmptySearchAdapter(this);
        resultList.setAdapter(emptySearchAdapter);
        searchProgress.setVisibility(View.INVISIBLE);
    }

    private void onSearchCompleted(List<StockQuote> list){
        ListView resultList = findViewById(R.id.search_result_list);

        if(list.size() == 0){
            setEmptySearchAdapter();
            return;
        }

        resultList.setAdapter(searchItemAdapter);
        stocksFragment.getData().clear();
        for(StockQuote stock : list)
            stocksFragment.getData().add(stock);

        searchItemAdapter.notifyDataSetChanged();
        searchProgress.setVisibility(View.INVISIBLE);
    }

    public class SearchTask extends AsyncTask<String, Void, List<StockQuote>> {
        private String errorMessage;

        private String getErrorMessage(){
            return errorMessage;
        }

        private void setErrorMessage(String message){
            errorMessage = message;
        }

        @Override
        protected List<StockQuote> doInBackground(String... params) {
            try{
                ApiConnector api = new WorldTradingConnector(getBaseContext());
                return api.searchEndpoint(params[0]);
            }
            catch (ApiException e){
                setErrorMessage(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<StockQuote> result) {
            super.onPostExecute(result);

            if(result == null)
                setErrorAdapter(getErrorMessage());
            else
                onSearchCompleted(result);

        }
    }
}
