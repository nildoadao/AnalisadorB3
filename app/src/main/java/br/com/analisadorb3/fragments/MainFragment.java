package br.com.analisadorb3.fragments;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.analisadorb3.R;
import br.com.analisadorb3.adapters.StockItemAdapter;
import br.com.analisadorb3.databinding.MainFragmentBinding;
import br.com.analisadorb3.models.StockRealTimeData;
import br.com.analisadorb3.viewmodel.MainViewModel;

public class MainFragment extends Fragment {

    private MainViewModel viewModel;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState == null){
            viewModel.init();
        }
        RecyclerView recyclerView = getView().findViewById(R.id.stock_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final StockItemAdapter adapter = new StockItemAdapter();
        recyclerView.setAdapter(adapter);

        viewModel.getSavedStocks().observe(this, new Observer<List<StockRealTimeData>>() {
            @Override
            public void onChanged(List<StockRealTimeData> stockRealTimeData) {
                adapter.setStocks(stockRealTimeData);
            }
        });
    }
}
