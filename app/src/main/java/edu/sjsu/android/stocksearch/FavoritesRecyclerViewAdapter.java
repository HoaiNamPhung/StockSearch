package edu.sjsu.android.stocksearch;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoritesRecyclerViewAdapter extends RecyclerView.Adapter<FavoritesRecyclerViewAdapter.ViewHolder> {

    private List<String[]> values;

    // Constructor
    public FavoritesRecyclerViewAdapter(List<String[]> myDataset) {
        values = myDataset;
    }

    // Provides a reference to the views for each provided data item.
    // ViewHolder will contain all the views of the data items.
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView stockName;
        public TextView stockPrice;
        public TextView stockChange;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            stockName = (TextView) v.findViewById(R.id.favStockSymbol);
            stockPrice = (TextView) v.findViewById(R.id.favStockPrice);
            stockChange = (TextView) v.findViewById(R.id.favMarketChange);
        }
    }

    public void add(int position, String[] item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    // Inflate your views. (invoked by the layout manager)
    // This sets each data item's view in accordance with row_layout.xml.
    @Override
    public FavoritesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewTop) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.fav_row_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view. (invoked by the layout manager)
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String[] stocks = values.get(position);
        holder.stockName.setText((String) stocks[0]);
        holder.stockPrice.setText((String) stocks[1]);
        holder.stockChange.setText((String) stocks[2]);
    }

    // Return the size of your data set. (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }
}
