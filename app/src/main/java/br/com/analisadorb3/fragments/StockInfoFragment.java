package br.com.analisadorb3.fragments;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.analisadorb3.R;
import br.com.analisadorb3.adapters.StockChartAdapter;
import br.com.analisadorb3.databinding.StockInfoFragmentBinding;
import br.com.analisadorb3.models.StockHistoricalData;
import br.com.analisadorb3.models.StockIntradayData;
import br.com.analisadorb3.util.StockUtil;
import br.com.analisadorb3.viewmodel.StockViewModel;

public class StockInfoFragment extends Fragment {

    private StockViewModel mViewModel;

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
        FloatingActionButton backButton = getView().findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        ViewPager viewPager = getView().findViewById(R.id.chart_view_pager);
        final StockChartAdapter adapter = new StockChartAdapter(getContext(), getFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = getView().findViewById(R.id.chart_tab);
        tabLayout.setupWithViewPager(viewPager);
        final TextView averageVariationText = getView().findViewById(R.id.day_change_average_3_value);

        mViewModel.getIntradayData().observe(this, new Observer<Map<String, StockIntradayData>>() {
            @Override
            public void onChanged(Map<String, StockIntradayData> stringStockIntradayDataMap) {
                adapter.setIntraDayData(stringStockIntradayDataMap);
            }
        });

        mViewModel.getDailyData().observe(this, new Observer<Map<String, StockHistoricalData>>() {
            @Override
            public void onChanged(Map<String, StockHistoricalData> maps) {
                averageVariationText.setText(mViewModel.getThreeMonthsVariation());
                adapter.setDailyData(maps);
            }
        });
    }
}
