package br.com.analisadorb3.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import br.com.analisadorb3.R;

public class StockItemHolder extends RecyclerView.ViewHolder {

    public TextView stockSymbol;
    public TextView stockPrice;
    public TextView stockChange;
    public ImageView changeArrow;
    public CardView stockCard;

    public StockItemHolder(@NonNull View itemView) {
        super(itemView);
        stockSymbol = itemView.findViewById(R.id.stock_name);
        stockPrice = itemView.findViewById(R.id.stock_info_price);
        stockChange = itemView.findViewById(R.id.stock_change_percent);
        changeArrow = itemView.findViewById(R.id.stock_arrow_status);
        stockCard = itemView.findViewById(R.id.stock_card);
    }
}
