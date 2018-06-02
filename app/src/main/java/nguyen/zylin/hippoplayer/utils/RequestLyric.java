package nguyen.zylin.hippoplayer.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Android Studio
 * Author: Duy-Linh Nguyen
 * Date: 02-Jun-18
 * Time: 18:29
 */
public class RequestLyric extends AsyncTask<String, Void, String> {

    private static final String TAG = "RequestLyric";

    public interface RequestListener{
        String doInBackgroundThread(String result);

        void doOnUIThread(String result);

        void onError(int errorCode, Exception e);

    }

    private RequestListener requestListener;
    private volatile boolean shouldRun;

    public RequestLyric(@NonNull RequestListener requestListener) {
        this.requestListener = requestListener;
        this.shouldRun = true;
    }

    @Override
    protected String doInBackground(String... strings) {
        int resultCode = 0;
        try {

            Log.d(TAG, "doInBackground: Request lyric: " + strings[0]);

            //Create request:
            URL url = new URL(strings[0]);
            HttpURLConnection connection = ((HttpURLConnection) url.openConnection());
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "text/html");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            connection.connect();

            if (!shouldRun) {
                return null;
            }

            //Get callback
            resultCode = connection.getResponseCode();
            StringBuilder stringBuilder = new StringBuilder();

            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while (shouldRun && (line = in.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }

            String result = stringBuilder.toString();
            if (shouldRun && requestListener != null) {
                return requestListener.doInBackgroundThread(result);
            } else {
                return result;
            }

        } catch (Exception e) {
            if (shouldRun && requestListener != null) {
                requestListener.onError(resultCode, e);
            }
        }

        return null;
    }

    public void stop() {
        shouldRun = false;
    }

    @Override
    protected void onPostExecute(String s) {
        if (shouldRun && requestListener != null) {
            requestListener.doOnUIThread(s);
        }
    }
}
