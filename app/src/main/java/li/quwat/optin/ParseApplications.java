package li.quwat.optin;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by a. z. quwatli on 8/7/2017.
 */

public class ParseApplications {
    private static final String TAG = "ParseApplications";

    private ArrayList<feedEntry> applications;

    public ParseApplications() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<feedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmlData) {
        boolean status = true;
        feedEntry currentRecord = null;
        boolean inEntry = false;
        String textValue = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int evenType = xpp.getEventType();
            while (evenType!=XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();
                switch (evenType) {
                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "parse: parse starting tag for "+tagName);
                        if("entry".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentRecord = new feedEntry();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "parse: ending tag for "+tagName);
                        if (inEntry) {
                            if ("entry".equalsIgnoreCase(tagName)) {
                                applications.add(currentRecord);
                                inEntry = false;
                            } else if ("name".equalsIgnoreCase(tagName)) {
                                currentRecord.setName(textValue);
                            } else if ("artist".equalsIgnoreCase(tagName)) {
                                currentRecord.setArtist(textValue);
                            } else if ("releaseDate".equalsIgnoreCase(tagName)) {
                                currentRecord.setReleaseDate(textValue);
                            } else if ("summary".equalsIgnoreCase(tagName)) {
                                currentRecord.setSummary(textValue);
                            } else if ("image".equalsIgnoreCase(tagName)) {
                                currentRecord.setImageURL(textValue);
                            }
                        }
                        break;
                    default:
                        //Nothing to do here!
                }
                evenType = xpp.next();

            }

            for (feedEntry app: applications) {
                Log.d(TAG, "=======================");
                Log.d(TAG, app.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }
}
