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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import br.com.analisadorb3.R;
import br.com.analisadorb3.adapters.StockChartAdapter;
import br.com.analisadorb3.databinding.StockInfoFragmentBinding;
import br.com.analisadorb3.models.YahooStockData;
import br.com.analisadorb3.util.SettingsUtil;
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

        final ViewPager viewPager = getView().findViewById(R.id.chart_view_pager);
        chartAdapter = new StockChartAdapter(getContext(), getFragmentManager());
        YahooStockData intraDayData = mViewModel.getIntraDayData().getValue();
        YahooStockData dailyData = mViewModel.getDailyData().getValue();
        chartAdapter.setIntraDayData(intraDayData);
        chartAdapter.setDailyData(dailyData);
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

        mViewModel.getSelectedStock().observe(this, new Observer<YahooStockData>() {
            @Override
            public void onChanged(YahooStockData stockRealTimeData) {
                mViewModel.updateView();
            }
        });

        mViewModel.getIntraDayData().observe(this, new Observer<YahooStockData>() {
            @Override
            public void onChanged(YahooStockData stringStockIntraDayDataMap) {
                chartAdapter.setIntraDayData(stringStockIntraDayDataMap);
                viewPager.setAdapter(chartAdapter);
            }
        });

        mViewModel.getDailyData().observe(this, new Observer<YahooStockData>() {
            @Override
            public void onChanged(YahooStockData maps) {
                chartAdapter.setDailyData(maps);
                viewPager.setAdapter(chartAdapter);

                if(maps != null)
                    mViewModel.updateView();
            }
        });

        final SwipeRefreshLayout refreshLayout = getView().findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.fetchData();
            }
        });

        mViewModel.isSearching().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                refreshLayout.setRefreshing(aBoolean);
            }
        });

        ImageButton followButton = getView().findViewById(R.id.follow_button);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedSymbol = SettingsUtil.getSelectedSymbol(getContext());
                if(SettingsUtil.getFavouriteStocks(getContext()).contains(selectedSymbol)){
                    if(mViewModel.unfollowStock(getActivity().getApplication(), selectedSymbol))
                        Toast.makeText(getActivity().getApplication(), getText(R.string.removed), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity().getApplication(), getText(R.string.remove_fail), Toast.LENGTH_LONG).show();
                }
                else {
                    if(mViewModel.followStock(getActivity().getApplication(), selectedSymbol))
                        Toast.makeText(getActivity().getApplication(), getText(R.string.saved), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity().getApplication(), getText(R.string.stocks_saved_limit), Toast.LENGTH_LONG).show();
                }
                mViewModel.updateView();
            }
        });

        if(savedInstanceState == null){
            mViewModel.fetchData();
        }
    }
}
