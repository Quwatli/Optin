package li.quwat.optin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = (ListView)findViewById(R.id.xmlListview);

        Log.d(TAG, "onCreate starting AsyncTask");

        dataDownload dd = new dataDownload();
        dd.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
        Log.d(TAG, "onCreate: done");
    }

    private class dataDownload extends AsyncTask<String, Void, String> {
        private static final String TAG = "dataDownload";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: parameter is "+s);
            ParseApplications parseApp = new ParseApplications();
            parseApp.parse(s);

            //ArrayAdapter<feedEntry> arrayAdapter = new ArrayAdapter<feedEntry>(MainActivity.this, R.layout.list_item, parseApp.getApplications());
            //listApps.setAdapter(arrayAdapter);

            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record, parseApp.getApplications());

            listApps.setAdapter(feedAdapter);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "doInBackground: starts with "+ params[0]);
            String rssFeed = downloadXML(params[0]);
            if (rssFeed == null) {
                Log.e(TAG, "doInBackground: ERROR DOWNLOADING");
            }


            return rssFeed;
        }

        private String downloadXML(String urlPath) {
            StringBuilder xmlResult = new StringBuilder();

            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: the response code was: "+ response);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charRead;
                char[] inputBuffer = new char[500];

                while (true) {
                    charRead = reader.read(inputBuffer);

                    if (charRead < 0) {
                        break;
                    }
                    if (charRead > 0) {
                        xmlResult.append(String.copyValueOf(inputBuffer,0,charRead));
                    }
                }

                reader.close();

                return xmlResult.toString();

            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadXML: INVALID URL "+e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "downloadXML: IOEXception reading data: "+e.getMessage());
            } catch (SecurityException e) {
                Log.e(TAG, "downloadXML: SecurityException, missing internet access permission"+e.getMessage() );
//                e.printStackTrace();
            }
            return null;
        }
    }
}
