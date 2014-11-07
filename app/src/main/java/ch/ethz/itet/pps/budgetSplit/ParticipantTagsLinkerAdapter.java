package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Chrissy on 06.11.2014.
 */
public class ParticipantTagsLinkerAdapter extends ArrayAdapter<ParticipantTagsLinker> {

    Context context;
    int layoutResourceId;
    ParticipantTagsLinker[] data = null;

    public ParticipantTagsLinkerAdapter(Context c, int l, ParticipantTagsLinker[] d) {
        super(c, l, d);
        this.context = c;
        this.layoutResourceId = l;
        this.data = d;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ParticipantTagsLinkerHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ParticipantTagsLinkerHolder();
            holder.name = (TextView) row.findViewById(R.id.summary_listview_participant_name);
            holder.expences = (TextView) row.findViewById(R.id.summary_listview_expences);
            holder.tags = (TextView) row.findViewById(R.id.summary_listview_tags);

            row.setTag(holder);
        } else {
            holder = (ParticipantTagsLinkerHolder) row.getTag();
        }

        ParticipantTagsLinker ptl = data[position];
        holder.name.setText(ptl.name);
        holder.expences.setText(ptl.money);
        holder.tags.setText(ptl.tags);

        return row;
    }

    static class ParticipantTagsLinkerHolder {
        TextView name;
        TextView tags;
        TextView expences;
    }
}
