package info.dolezel.fatrat.android;

import android.os.Message;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.nmote.xr.XR;
import com.nmote.xr.Endpoint;

/**
 * Created by lubos on 9/22/14.
 */
public class DataDownloadTask implements Runnable {
    private static final String TAG = "DataDownloadTask";
    private MainActivity activity;
    private static RpcInterface iface;
    private String queueUUID;
    private boolean refreshQueues;

    static {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            // Trust always
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            // Trust always
                        }
                    }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("TLS");
            // Create empty HostnameVerifier
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            };

            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            SSLContext.setDefault(sc);

        } catch (Exception e) {
            Log.e(TAG, "Error setting up HTTPS", e);
        }
    }

    public DataDownloadTask(MainActivity activity, boolean refreshQueues, String queueUUID) {
        this.activity = activity;
        this.refreshQueues = refreshQueues;
        this.queueUUID = queueUUID;
    }

    @Override
    public void run() {
        try {
            if (iface == null) {
                String url, password;

                url = activity.prefs.getString("serverUrl", null);
                password = activity.prefs.getString("password", null);

                createInterface(url, password);
            }

            if (refreshQueues) {
                List<Map<String,String>> queues;

                queues = iface.getQueues();

                Log.v(TAG, "List of queues: " + queues);

                Message msg = new Message();
                msg.what = MainActivity.MSG_QUEUES;
                msg.obj = queues;
                activity.handler.dispatchMessage(msg);
            }

            if (queueUUID != null) {
                List<Map<String,String>> transfers;

                transfers = iface.getTransfers("{" + queueUUID + "}");

                Log.v(TAG, "List of transfers: " + transfers);

                Message msg = new Message();
                msg.what = MainActivity.MSG_TRANSFERS;
                msg.obj = transfers;
                activity.handler.dispatchMessage(msg);
            }
        } catch (Exception e) {
            activity.showError(e);
        }
    }

    public static void invalidateInterface() {
        iface = null;
    }

    private static void createInterface(String textUrl, String password) throws Exception {
        URL url = new URL(textUrl + "/xmlrpc");

        if (password != null) {
            url = new URL(url.getProtocol() + "://admin:" + password + "@" + url.getHost() + ":" + url.getPort() + url.getPath());
        }

        Log.d(TAG, "Access URL: " + url);

        iface = (RpcInterface) XR.proxy(url, RpcInterface.class);
    }
}
