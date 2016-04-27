package hr.droidcon.conference.hack.adapters;

import android.content.Context;
import android.graphics.Color;
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
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
            holder.headline.setTextColor(Color.parseColor("#5B5B5B"));
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        try {
            Date startDate = dateFormat.parse(object.getStartDate());
            Date endDate = dateFormat.parse(object.getEndDate());
            String time = simpleDateFormat.format(startDate) + simpleDateFormat2.format(endDate);
            holder.dateStart.setText(time);
            holder.dateStart.setTextColor(Color.parseColor("#5B5B5B"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        v.setBackgroundColor(Color.parseColor("#E2E2E2"));

        holder.headline.setText(object.getHeadline());

        return v;
    }

    private class ViewHolder {
        TextView dateStart;
        TextView headline;
    }
}
