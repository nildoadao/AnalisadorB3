package br.com.analisadorb3.fragments;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Map;

import br.com.analisadorb3.R;
import br.com.analisadorb3.adapters.StockChartAdapter;
import br.com.analisadorb3.databinding.StockInfoFragmentBinding;
import br.com.analisadorb3.models.StockHistoricalData;
import br.com.analisadorb3.models.StockIntraDayData;
import br.com.analisadorb3.models.StockRealTimeData;
import br.com.analisadorb3.viewmodel.StockViewModel;

public class StockInfoFragment extends Fragment {

    private StockViewModel mViewModel;
    private StockChartAdapter chartAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        StockInfoFragmentBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.stock_info_fragment, container,  false);
        mViewModel = ViewModelProviders.of(this).get(StockViewModel.class);
        binding.setViewModel(mViewModel);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewPager viewPager = getView().findViewById(R.id.chart_view_pager);
        chartAdapter = new StockChartAdapter(getContext(), getFragmentManager());
        viewPager.setAdapter(chartAdapter);
        TabLayout tabLayout = getView().findViewById(R.id.chart_tab);
        tabLayout.setupWithViewPager(viewPager);
        FloatingActionButton backButton = getView().findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mViewModel.getSelectedStock().observe(this, new Observer<StockRealTimeData>() {
            @Override
            public void onChanged(StockRealTimeData stockRealTimeData) {
                mViewModel.fetchData();
            }
        });

        mViewModel.getIntraDayData().observe(this, new Observer<Map<String, StockIntraDayData>>() {
            @Override
            public void onChanged(Map<String, StockIntraDayData> stringStockIntraDayDataMap) {
                chartAdapter.setIntraDayData(stringStockIntraDayDataMap);
                chartAdapter.setStatus(mViewModel.getStockStatus());
            }
        });

        final TextView averageVariationText = getView().findViewById(R.id.day_change_average_3_value);
        mViewModel.getDailyData().observe(this, new Observer<Map<String, StockHistoricalData>>() {
            @Override
            public void onChanged(Map<String, StockHistoricalData> maps) {
                averageVariationText.setText(mViewModel.getThreeMonthsVariation());
                chartAdapter.setDailyData(maps);
                chartAdapter.setStatus(mViewModel.getStockStatus());
            }
        });

        final SwipeRefreshLayout refreshLayout = getView().findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.setSelectedStock(mViewModel.getSelectedStock().getValue());
            }
        });

        mViewModel.isSearching().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                refreshLayout.setRefreshing(aBoolean);
            }
        });

        if(savedInstanceState == null)
            mViewModel.fetchData();
    }

}
