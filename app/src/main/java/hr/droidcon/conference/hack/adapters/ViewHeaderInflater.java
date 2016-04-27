package hr.droidcon.conference.hack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hr.droidcon.conference.hack.R;
import hr.droidcon.conference.hack.objects.Conference;

/**
 * Render a break/lunch for a Conference object given, every time {@link this#getView(
 *hr.droidcon.conference.hack.objects.Conference, int, android.view.View, android.view.ViewGroup)}
 * is called.
 *
 * @author Arnaud Camus
 */
public class ViewHeaderInflater extends ItemInflater<Conference> {

    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat simpleDateFormat2;
    private SimpleDateFormat dateFormat;
    public ViewHeaderInflater(Context ctx) {
        super(ctx);
        simpleDateFormat = new SimpleDateFormat("E, HH:mm");
        simpleDateFormat2 = new SimpleDateFormat(" - HH:mm");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
    }

    @Override
    public View getView(Conference object, int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.adapter_header, parent, false);

            holder = new ViewHolder();
            holder.dateStart = (TextView) v.findViewById(R.id.dateStart);
            holder.headline = (TextView) v.findViewById(R.id.headline);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        try {
            Date startDate = dateFormat.parse(object.getStartDate());
            Date endDate = dateFormat.parse(object.getEndDate());
            String time = simpleDateFormat.format(startDate) + simpleDateFormat2.format(endDate);
            holder.dateStart.setText(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (object.getHeadline().contentEquals("DAY 2")) {
            v.setBackgroundColor(parent.getContext().getResources().getColor(R.color.pauseColorDay2));
        } else {
            v.setBackgroundColor(parent.getContext().getResources().getColor(R.color.pauseColorDay1));
        }
        holder.headline.setText(object.getHeadline());

        return v;
    }

    private class ViewHolder {
        TextView dateStart;
        TextView headline;
    }
}
