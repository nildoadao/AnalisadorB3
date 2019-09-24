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
import br.com.analisadorb3.models.StockSearchResult;
import br.com.analisadorb3.util.SettingsUtil;

public class StockSearchAdapter extends ListAdapter<StockSearchResult, StockSearchAdapter.SearchResultHolder> {

    public StockSearchAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<StockSearchResult> DIFF_CALLBACK = new DiffUtil.ItemCallback<StockSearchResult>() {
        @Override
        public boolean areItemsTheSame(@NonNull StockSearchResult oldItem, @NonNull StockSearchResult newItem) {
            return oldItem.getSymbol().equals(newItem.getSymbol());
        }

        @Override
        public boolean areContentsTheSame(@NonNull StockSearchResult oldItem, @NonNull StockSearchResult newItem) {
            return oldItem.getSymbol().equals(newItem.getSymbol()) &&
                    oldItem.getName().equals(newItem.getName());
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
        void onItemClick(StockSearchResult stock);
    }

    public interface OnFollowButtonClickListener{
        void onFollowButtonClick(StockSearchResult stock);
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
        StockSearchResult currentStock = getItem(position);
        holder.symbolTextView.setText(currentStock.getSymbol());
        holder.companyTextView.setText(currentStock.getName());
        if(SettingsUtil.getFavouriteStocks(holder.statusButton.getContext()).contains(currentStock.getSymbol()))
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
