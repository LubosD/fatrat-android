package info.dolezel.fatrat.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import info.dolezel.fatrat.android.adapters.QueueAdapter;
import info.dolezel.fatrat.android.adapters.TransferAdapter;
import info.dolezel.fatrat.android.data.Queue;
import info.dolezel.fatrat.android.data.Transfer;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String PREF_URL = "serverUrl";
    private static final int REQUEST_SETTINGS = 1;

    public static final int MSG_REFRESH = 1;
    public static final int MSG_QUEUES = 2;
    public static final int MSG_TRANSFERS = 3;

    SharedPreferences prefs;
    private boolean restartAfterSettingsClosed;
    private ExecutorService commExecutor = Executors.newSingleThreadExecutor();

    Spinner spinnerQueues;
    ListView listTransfers;

    List<Queue> queues;
    QueueAdapter queueAdapter;
    int currentQueue = -1;

    List<Transfer> transfers;
    TransferAdapter transferAdapter;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case MSG_REFRESH:
                    refresh();
                    break;
                case MSG_QUEUES:
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            updateQueueList((List<Map<String, Object>>) msg.obj);
                        }
                    });

                    this.sendEmptyMessageDelayed(MSG_REFRESH, 5000);
                    break;
                case MSG_TRANSFERS:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateTransferList((List<Map<String, Object>>) msg.obj);
                        }
                    });
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getAll().containsKey(PREF_URL)) {
            startSettings(true);
        } else {
            setContentView(R.layout.activity_main);

            queueAdapter = new QueueAdapter(this);
            spinnerQueues = (Spinner) findViewById(R.id.spinner);
            spinnerQueues.setAdapter(queueAdapter);

            transferAdapter = new TransferAdapter(this);
            listTransfers = (ListView) findViewById(R.id.listTransfers);
            listTransfers.setAdapter(transferAdapter);

            spinnerQueues.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int index, long id) {
                    currentQueue = index;
                    queueAdapter.setSpeed(0, 0);
                    refreshTransfers();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    currentQueue = -1;
                    transferAdapter.setTransfers(null);
                    refreshTransfers();
                }
            });

            refresh();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(MSG_REFRESH);
    }

    private void startSettings(boolean restartAfterClose) {
        restartAfterSettingsClosed = restartAfterClose;

        startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SETTINGS) {
            if (restartAfterSettingsClosed)
                recreate();
            restartAfterSettingsClosed = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_SETTINGS);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        String uuid = null;

        if (currentQueue != -1)
            uuid = queues.get(currentQueue).uuid.toString();

        commExecutor.submit(new DataDownloadTask(this, true, uuid));
    }

    private void refreshTransfers() {
        if (currentQueue != -1) {
            Log.d(TAG, "Download transfers for queue " + currentQueue);
            commExecutor.submit(new DataDownloadTask(this, false, queues.get(currentQueue).uuid.toString()));
        }
    }

    void showError(Exception e) {
        Log.e(TAG, "Displaying network error", e);

        if (!isNetworkAvailable()) {

        } else {

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void updateQueueList(List<Map<String, Object>> newQueues) {
        List<Queue> queueList = new ArrayList<Queue>(newQueues.size());

        for (Map<String,Object> map : newQueues) {
            queueList.add(Queue.fromMap(map));
        }

        queueAdapter.setQueues(queueList);
        queues = queueList;
    }

    private void updateTransferList(List<Map<String, Object>> newQueues) {
        List<Transfer> transferList = new ArrayList<Transfer>(newQueues.size());
        int totalDown = 0, totalUp = 0;

        for (Map<String,Object> map : newQueues) {
            Transfer t = Transfer.fromMap(map);
            transferList.add(t);

            totalDown += t.speedDown;
            totalUp += t.speedUp;
        }

        transferAdapter.setTransfers(transferList);
        queueAdapter.setSpeed(totalDown, totalUp);
        transfers = transferList;
    }
}
