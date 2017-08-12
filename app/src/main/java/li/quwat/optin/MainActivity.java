package li.quwat.optin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    private String feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    private String feedCachedURL = "INVALIDATED";
    public static final String STATE_URL = "feedURL";
    public static final String STATE_LIMIT = "feedLimit";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = (ListView)findViewById(R.id.xmlListview);

        if (savedInstanceState != null) {
            feedURL = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }

        downloadURL(String.format(feedURL,feedLimit));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.feedmenu, menu);

        if (feedLimit == 10) {
            menu.findItem(R.id.top10).setChecked(true);
        } else {
            menu.findItem(R.id.top25).setChecked(true);
        }
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        switch (id) {
            case R.id.menuFree:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.menuPaid:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.menuSongs:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.top10:
            case R.id.top25:
                if(!item.isChecked()) {
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feedLimit to " + feedLimit);
                } else {
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " feedLimit unchanged");
                }
                break;
            case R.id.refresh:
                feedCachedURL = "INVALIDATED";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        downloadURL(String.format(feedURL, feedLimit));
        return true;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedURL);
        outState.putInt(STATE_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }


    private void downloadURL(String url) {
        if (!feedURL.equalsIgnoreCase(feedCachedURL)){
            dataDownload dd = new dataDownload();
            dd.execute(url);
            feedCachedURL = url;
            Log.d(TAG, "onCreate: done");
    } else {
            Log.d(TAG, "downloadURL: URL not changed");
        }
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
