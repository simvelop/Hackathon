package hr.droidcon.conference.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hr.droidcon.conference.BaseApplication;
import hr.droidcon.conference.R;
import hr.droidcon.conference.events.FilterUpdateEvent;
import hr.droidcon.conference.objects.Conference;
import hr.droidcon.conference.utils.WordColor;

/**
 * Render a Conference object every time {@link this#getView(
 *hr.droidcon.conference.objects.Conference, int, android.view.View, android.view.ViewGroup)}
 * is called.
 *
 * @author Arnaud Camus
 */
public class ViewConferenceInflater extends ItemInflater<Conference> {

    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat dateFormat;

    public ViewConferenceInflater(Context ctx) {
        super(ctx);
        simpleDateFormat = new SimpleDateFormat("E, HH:mm");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
    }

    @Override
    public View getView(final Conference conference, int position, View convertView, final ViewGroup parent) {
        View v = convertView;
        final ViewHolder holder;
        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.adapter_conference, parent, false);

            holder = new ViewHolder();
            holder.dateStart = (TextView) v.findViewById(R.id.dateStart);
            holder.location = (TextView) v.findViewById(R.id.location);
            holder.headline = (TextView) v.findViewById(R.id.headline);
            holder.speaker = (TextView) v.findViewById(R.id.speaker);
            holder.image = (ImageView) v.findViewById(R.id.image);
            holder.favorite = (ImageView) v.findViewById(R.id.favorite);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        try {
            Date startDate = dateFormat.parse(conference.getStartDate());
            holder.dateStart.setText(simpleDateFormat.format(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.location.setText(String.format(mContext.getString(R.string.location),
                conference.getLocation()));
        holder.location.setTextColor(WordColor.generateColor(conference.getLocation()));
        holder.headline.setText(Html.fromHtml(conference.getHeadline()));
        holder.speaker.setText(Html.fromHtml(conference.getSpeaker()));

        // picasso
        Picasso.with(mContext.getApplicationContext())
                .load(conference.getSpeakerImageUrl())
                .transform(((BaseApplication) mContext.getApplicationContext()).mPicassoTransformation)
                .into(holder.image);
        holder.favorite.setImageResource(conference.isFavorite(mContext)
                ? R.drawable.ic_favorite_grey600_18dp
                : R.drawable.ic_favorite_outline_grey600_18dp);

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conference.toggleFavorite(mContext);
                holder.favorite.setImageResource(conference.isFavorite(mContext)
                        ? R.drawable.ic_favorite_grey600_18dp
                        : R.drawable.ic_favorite_outline_grey600_18dp);
                boolean filterSetting = ((BaseApplication) mContext.getApplicationContext()).isFilterFavorites();
                EventBus.getDefault().post(new FilterUpdateEvent(filterSetting));
            }
        });

        return v;
    }

    private class ViewHolder {
        TextView dateStart;
        TextView location;
        TextView headline;
        TextView speaker;
        ImageView image;
        ImageView favorite;
    }
}
