package li.quwat.optin;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by a. z. quwatli on 8/12/2017.
 */

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<feedEntry> applications;

    public FeedAdapter(@NonNull Context context, @LayoutRes int resource, List<feedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }



    @Override
    public int getCount() {
        return applications.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = layoutInflater.inflate(layoutResource, parent, false);
        TextView tvname = (TextView)view.findViewById(R.id.tvName);
        TextView tvartist = (TextView)view.findViewById(R.id.tvArtist);
        TextView tvsummary = (TextView)view.findViewById(R.id.tvSummary);

        feedEntry currentApp = applications.get(position);

        tvname.setText(currentApp.getName());
        tvartist.setText(currentApp.getArtist());
        tvsummary.setText(currentApp.getSummary());

        return view;
    }
}
