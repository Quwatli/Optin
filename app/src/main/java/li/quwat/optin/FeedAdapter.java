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

        ViewHolder vh;
        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);

            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder)convertView.getTag();
        }


        feedEntry currentApp = applications.get(position);

        vh.tvName.setText(currentApp.getName());
        vh.tvArtist.setText(currentApp.getArtist());
        vh.tvSummary.setText(currentApp.getSummary());

        return convertView;
    }

    private class ViewHolder {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        public ViewHolder(View v) {
            this.tvName = (TextView)v.findViewById(R.id.tvName);
            this.tvArtist = (TextView)v.findViewById(R.id.tvArtist);
            this.tvSummary = (TextView)v.findViewById(R.id.tvSummary);
        }
    }
}
