package br.com.analisadorb3.fragments;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.analisadorb3.R;
import br.com.analisadorb3.adapters.EmptyWalletAdapter;
import br.com.analisadorb3.adapters.StockItemAdapter;
import br.com.analisadorb3.databinding.MainFragmentBinding;
import br.com.analisadorb3.models.StockRealTimeData;
import br.com.analisadorb3.viewmodel.MainViewModel;

public class MainFragment extends Fragment {

    private MainViewModel viewModel;
    private EmptyWalletAdapter emptyWalletAdapter = new EmptyWalletAdapter();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainFragmentBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.main_fragment, container, false);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final RecyclerView recyclerView = getView().findViewById(R.id.stock_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final StockItemAdapter adapter = new StockItemAdapter();
        recyclerView.setAdapter(adapter);
        final SwipeRefreshLayout refreshLayout = getView().findViewById(R.id.stock_swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.updateStocks();
            }
        });

        viewModel.isRefreshing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                refreshLayout.setRefreshing(aBoolean);
            }
        });

        viewModel.getFavouriteStocks().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                viewModel.updateStocks();
            }
        });

        viewModel.getSavedStocks().observe(this, new Observer<List<StockRealTimeData>>() {
            @Override
            public void onChanged(List<StockRealTimeData> stockRealTimeData) {
                if(stockRealTimeData == null){
                    recyclerView.setAdapter(emptyWalletAdapter);
                }
                else {
                    recyclerView.setAdapter(adapter);
                    adapter.submitList(stockRealTimeData);
                }
            }
        });

        adapter.setOnItemClickListener(new StockItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Context context, int position) {
                viewModel.setSelectedStock(adapter.getStockAt(position));
                Navigation.findNavController(getView()).navigate(R.id.stockInfoFragment);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                viewModel.unfollowStock(adapter.getStockAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getActivity(), getText(R.string.removed), Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        FloatingActionButton searchButton = getView().findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.searchFragment);
            }
        });
        if(savedInstanceState == null){
            viewModel.updateStocks();
        }
    }
}
