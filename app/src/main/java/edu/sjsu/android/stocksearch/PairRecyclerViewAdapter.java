package edu.sjsu.android.stocksearch;


import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PairRecyclerViewAdapter extends RecyclerView.Adapter<PairRecyclerViewAdapter.ViewHolder> {
    
    private List<Pair> values;

    // Constructor
    public PairRecyclerViewAdapter(List<Pair> myDataset) {
        values = myDataset;
    }

    // Provides a reference to the views for each provided data item.
    // ViewHolder will contain all the views of the data items.
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtHeader;
        public TextView txtFooter;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
            txtFooter = (TextView) v.findViewById(R.id.secondLine);
        }
    }

    public void add(int position, Pair item) {
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
    public PairRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewTop) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view. (invoked by the layout manager)
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Pair pair = values.get(position);
        holder.txtHeader.setText((String) pair.first);
        holder.txtFooter.setText((String) pair.second);
    }

    // Return the size of your data set. (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }
}
