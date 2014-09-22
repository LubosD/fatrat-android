package info.dolezel.fatrat.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import info.dolezel.fatrat.android.R;
import info.dolezel.fatrat.android.data.Queue;
import info.dolezel.fatrat.plugins.util.FormatUtils;

/**
 * Created by lubos on 9/22/14.
 */
public class QueueAdapter implements SpinnerAdapter {
    private final LayoutInflater layoutInflater;
    private List<Queue> queues = Collections.emptyList();
    private Set<DataSetObserver> observers = new HashSet<DataSetObserver>();
    private int speedDown, speedUp;

    private static class Row {
        TextView queueName, downloadSpeed, uploadSpeed;
    }

    public QueueAdapter(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
    }

    public List<Queue> getQueues() {
        return queues;
    }

    public void setQueues(List<Queue> queues) {
        if (queues == null)
            queues = Collections.emptyList();

        this.queues = queues;
        for (DataSetObserver obs : observers) {
            obs.onChanged();
        }
    }

    public void setSpeed(int down, int up) {
        speedDown = down;
        speedUp = up;

        for (DataSetObserver obs : observers) {
            obs.onChanged();
        }
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        TextView tv = (TextView) view;

        if (tv == null)
            tv = new TextView(viewGroup.getContext());

        tv.setText(queues.get(i).name);
        tv.setPadding(10, 10, 10, 10);

        return tv;
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
        return queues.size();
    }

    @Override
    public Object getItem(int i) {
        return queues.get(i);
    }

    @Override
    public long getItemId(int i) {
        return queues.get(i).uuid.getLeastSignificantBits();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Row row;
        Queue queue = queues.get(i);

        if (view != null)
            row = (Row) view.getTag();
        else {
            row = new Row();
            view = layoutInflater.inflate(R.layout.row_queue, null);

            row.queueName = (TextView) view.findViewById(R.id.textName);
            row.downloadSpeed = (TextView) view.findViewById(R.id.textDownload);
            row.uploadSpeed = (TextView) view.findViewById(R.id.textUpload);
            view.setTag(row);
        }

        row.queueName.setText(queue.name);
        row.downloadSpeed.setText(FormatUtils.formatSize(speedDown) + "/s");
        row.uploadSpeed.setText(FormatUtils.formatSize(speedUp) + "/s");

        return view;
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
        return queues.isEmpty();
    }
}
