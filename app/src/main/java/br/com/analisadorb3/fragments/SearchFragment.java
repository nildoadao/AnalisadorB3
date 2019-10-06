package br.com.analisadorb3.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.analisadorb3.R;
import br.com.analisadorb3.adapters.StockSearchAdapter;
import br.com.analisadorb3.models.StockSearchResult;
import br.com.analisadorb3.util.SettingsUtil;
import br.com.analisadorb3.viewmodel.StockViewModel;

public class SearchFragment extends Fragment {

    StockViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(StockViewModel.class);
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final RecyclerView recyclerView = getView().findViewById(R.id.search_recycler_view);
        final ProgressBar searching = getView().findViewById(R.id.search_progress_bar);
        final StockSearchAdapter adapter = new StockSearchAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnFollowButtonClickListener(new StockSearchAdapter.OnFollowButtonClickListener() {
            @Override
            public void onFollowButtonClick(StockSearchResult stock) {
                if(SettingsUtil.getFavouriteStocks(getContext()).contains(stock.getSymbol())){
                    if(viewModel.unfollowStock(getActivity().getApplication(), stock.getSymbol()))
                        Toast.makeText(getActivity().getApplication(), getText(R.string.removed), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity().getApplication(), getText(R.string.remove_fail), Toast.LENGTH_LONG).show();
                }
                else {
                    if(viewModel.followStock(getActivity().getApplication(), stock.getSymbol()))
                        Toast.makeText(getActivity().getApplication(), getText(R.string.saved), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity().getApplication(), getText(R.string.stocks_saved_limit), Toast.LENGTH_LONG).show();
                }
                viewModel.updateView();
            }
        });

        adapter.setOnItemClickListener(new StockSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(StockSearchResult stock) {
                viewModel.setSelectedStock(stock.getSymbol());
                Navigation.findNavController(getView()).navigate(R.id.stockInfoFragment);
            }
        });

        viewModel.isSearching().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    searching.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
                else{
                    searching.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getSearchResults().observe(this, new Observer<List<StockSearchResult>>() {
            @Override
            public void onChanged(List<StockSearchResult> stockSearchResults) {
                adapter.submitList(stockSearchResults);
            }
        });

        viewModel.getFavouriteStocks().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                adapter.notifyDataSetChanged();
            }
        });

        FloatingActionButton backButton = getView().findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        final EditText searchEditText = getView().findViewById(R.id.search_edit_text);
        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                        i == KeyEvent.KEYCODE_ENTER){
                    viewModel.search(searchEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        if(searchEditText.requestFocus()){
            InputMethodManager input = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            input.showSoftInput(getView(), InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
