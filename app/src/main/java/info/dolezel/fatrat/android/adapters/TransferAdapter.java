package info.dolezel.fatrat.android.adapters;

import android.app.Activity;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.dolezel.fatrat.android.R;
import info.dolezel.fatrat.android.data.Transfer;

/**
 * Created by lubos on 9/22/14.
 */
public class TransferAdapter implements ListAdapter {
    private Set<DataSetObserver> observers = new HashSet<DataSetObserver>();
    private List<Transfer> transfers = Collections.emptyList();
    private LayoutInflater layoutInflater;

    private static class Row {
        ImageView imageState;
        TextView textName;
    }

    public TransferAdapter(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
    }

    public List<Transfer> getTransfers() {
        return transfers;
    }

    public void setTransfers(List<Transfer> transfers) {
        if (transfers == null)
            transfers = Collections.emptyList();

        this.transfers = transfers;

        for (DataSetObserver obs : observers)
            obs.onChanged();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        observers.add(dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        observers.remove(dataSetObserver);
    }

    @Override
    public int getCount() {
        return transfers.size();
    }

    @Override
    public Object getItem(int i) {
        return transfers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return transfers.get(i).uuid.getLeastSignificantBits();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Row row;

        if (view != null)
            row = (Row) view.getTag();
        else {
            row = new Row();
            view = layoutInflater.inflate(R.layout.row_transfer, null);
            view.setTag(row);

            row.imageState = (ImageView) view.findViewById(R.id.imageState);
            row.textName = (TextView) view.findViewById(R.id.textName);
        }

        int resImg;
        Transfer t = transfers.get(i);

        row.textName.setText(t.name);

        resImg = iconForModeAndState(t.mode, t.primaryMode, t.state);
        row.imageState.setImageResource(resImg);

        return view;
    }

    private static int iconForModeAndState(Transfer.Mode mode, Transfer.Mode primaryMode, Transfer.State state) {
        final boolean upload = mode == Transfer.Mode.Upload;
        final boolean primaryUpload = primaryMode == Transfer.Mode.Upload;

        switch (state) {
            case Active:
                return upload ? R.drawable.distribute : R.drawable.active;
            case ForcedActive:
                return upload ? R.drawable.forced_distribute : R.drawable.forced_active;
            case Paused:
                return upload ? R.drawable.paused_upload : R.drawable.paused;
            case Waiting:
                return upload ? R.drawable.waiting_upload : R.drawable.waiting;
            case Completed:
                return primaryUpload ? R.drawable.completed_upload : R.drawable.completed;
            default:
                return 0;
        }
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return transfers.isEmpty();
    }
}
