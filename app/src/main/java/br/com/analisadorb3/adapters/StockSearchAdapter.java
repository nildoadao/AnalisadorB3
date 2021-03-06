package br.com.analisadorb3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import br.com.analisadorb3.R;
import br.com.analisadorb3.models.YahooStockData;
import br.com.analisadorb3.util.SettingsUtil;

public class StockSearchAdapter extends ListAdapter<YahooStockData, StockSearchAdapter.SearchResultHolder> {

    public StockSearchAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<YahooStockData> DIFF_CALLBACK = new DiffUtil.ItemCallback<YahooStockData>() {
        @Override
        public boolean areItemsTheSame(@NonNull YahooStockData oldItem, @NonNull YahooStockData newItem) {
            return oldItem.getChart().getResult().get(0).getMeta().getSymbol().equals(newItem.getChart().getResult().get(0).getMeta().getSymbol());
        }

        @Override
        public boolean areContentsTheSame(@NonNull YahooStockData oldItem, @NonNull YahooStockData newItem) {
            return oldItem.getChart().getResult().get(0).getMeta().getSymbol().equals(newItem.getChart().getResult().get(0).getMeta().getSymbol()) &&
                    oldItem.getChart().getResult().get(0).getMeta().getSymbol().equals(newItem.getChart().getResult().get(0).getMeta().getSymbol());
        }
    };

    class SearchResultHolder extends RecyclerView.ViewHolder {
        TextView symbolTextView;
        TextView companyTextView;
        ImageButton statusButton;

        public SearchResultHolder(@NonNull View itemView) {
            super(itemView);
            symbolTextView = itemView.findViewById(R.id.symbol_text_view);
            companyTextView = itemView.findViewById(R.id.company_text_view);
            statusButton = itemView.findViewById(R.id.status_button);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onItemClick(getItem(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(YahooStockData stock);
    }

    public interface OnFollowButtonClickListener{
        void onFollowButtonClick(YahooStockData stock);
    }

    private OnItemClickListener clickListener;
    private OnFollowButtonClickListener followButtonClickListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        clickListener = listener;
    }

    public void setOnFollowButtonClickListener(OnFollowButtonClickListener listener){
        followButtonClickListener = listener;
    }

    @NonNull
    @Override
    public SearchResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item_result, parent, false);
        return new SearchResultHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultHolder holder, final int position) {
        YahooStockData currentStock = getItem(position);
        holder.symbolTextView.setText(currentStock.getChart().getResult().get(0).getMeta().getSymbol());
        holder.companyTextView.setText(currentStock.getChart().getResult().get(0).getMeta().getSymbol());
        if(SettingsUtil.getFavouriteStocks(holder.statusButton.getContext()).contains(currentStock.getChart().getResult().get(0).getMeta().getSymbol()))
            holder.statusButton.setImageResource(R.drawable.star_icon);
        else
            holder.statusButton.setImageResource(R.drawable.plus_icon);

        holder.statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(followButtonClickListener != null && position != RecyclerView.NO_POSITION)
                    followButtonClickListener.onFollowButtonClick(getItem(position));
            }
        });
    }
}
