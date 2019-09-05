package br.com.analisadorb3.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import br.com.analisadorb3.R;
import br.com.analisadorb3.databinding.MainFragmentBinding;
import br.com.analisadorb3.fragments.MainFragment;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.viewmodel.MainViewModel;

public class StockItemHolder extends RecyclerView.ViewHolder {

    MainFragmentBinding binding;

    public StockItemHolder(MainFragmentBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(MainViewModel viewModel, int position){
        binding.setVariable(br.com.analisadorb3.BR.viewmodel, viewModel);
        binding.setVariable()
    }
}
