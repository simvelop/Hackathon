package hr.droidcon.conference.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import hr.droidcon.conference.BaseApplication;
import hr.droidcon.conference.ConferenceActivity;
import hr.droidcon.conference.R;
import hr.droidcon.conference.database.DatabaseKt;
import hr.droidcon.conference.events.FilterUpdateEvent;
import hr.droidcon.conference.objects.Conference;
import hr.droidcon.conference.utils.DeviceInfoKt;
import hr.droidcon.conference.utils.WordColor;
import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by stefan.tanovic on 4/27/2016.
 */
public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.ViewHolder> {

    public static final int COUNT_VIEWS = 2;

    public static final int VIEW_HEADER = 0;

    public static final int VIEW_CONFERENCE = 1;

    private final Activity activity;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, HH:mm");

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

    private List<Conference> conferences;
    //    private ViewHeaderInflater viewHeaderInflater;
    //    private ViewConferenceInflater viewConferenceInflater;

    public FilterListAdapter(Activity activity, int resource, List<Conference> objects) {
        this.activity = activity;
        conferences = objects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.adapter_conference, parent, false);

        ViewHolder holder = new ViewHolder(v);
        holder.dateStart = (TextView) v.findViewById(R.id.dateStart);
        holder.location = (TextView) v.findViewById(R.id.location);
        holder.headline = (TextView) v.findViewById(R.id.headline);
        holder.speaker = (TextView) v.findViewById(R.id.speaker);
        holder.image = (ImageView) v.findViewById(R.id.image);
        holder.favorite = (ImageView) v.findViewById(R.id.favorite);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Conference object = getItem(position);
        try {
            Date startDate = dateFormat.parse(object.getStartDate());
            holder.dateStart.setText(simpleDateFormat.format(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.location.setText(String.format(activity.getString(R.string.location),
                object.getLocation()));
        holder.location.setTextColor(WordColor.generateColor(object.getLocation()));
        holder.headline.setText(Html.fromHtml(object.getHeadline()));
        holder.speaker.setText(Html.fromHtml(object.getSpeaker()));

        // picasso
        Picasso.with(activity.getApplicationContext())
               .load(object.getSpeakerImageUrl())
               .transform(((BaseApplication) activity.getApplicationContext())
                       .mPicassoTransformation)
               .into(holder.image);

        holder.favorite.setImageResource(object.isFavorite(activity)
                ? R.drawable.ic_favorite_grey600_18dp
                : R.drawable.ic_favorite_outline_grey600_18dp);

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext().getApplicationContext();

                boolean isFav = object.toggleFavorite(context);
                holder.favorite.setImageResource(object.isFavorite(activity)
                        ? R.drawable.ic_favorite_grey600_18dp
                        : R.drawable.ic_favorite_outline_grey600_18dp);

                boolean filterSetting = ((BaseApplication) context).isFilterFavorites();
                EventBus.getDefault().post(new FilterUpdateEvent(filterSetting));
                DatabaseKt.setFavorite(
                        object.getConferenceId(),
                        DeviceInfoKt.getDeviceId(context),
                        isFav
                );
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (object.getSpeaker().length() == 0) {
                    // if the speaker field is empty, it's probably a coffee break or lunch
                    return;
                }

                // TODO: FIX
                // On Lollipop we animate the speaker's name & picture
                // to the second activity
                //        Pair<View, String> toolbar = Pair.create((View) mToolbar,
                //                getString(R.string.toolbar));
                Pair<ImageView, String> image = Pair.create(holder.image,
                        activity.getString(R.string.image));
                Pair<TextView, String> speaker = Pair.create(holder.speaker,
                        activity.getString(R.string.speaker));
                //                Bundle bundle = ActivityOptionsCompat
                // .makeSceneTransitionAnimation(activity, image, speaker).toBundle();
                Bundle bundle = new Bundle();
                Intent intent = new Intent(activity, ConferenceActivity.class);
                intent.putExtra("conference", object);
                ActivityCompat.startActivity(activity, intent, bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return conferences.size();
    }

    public Conference getItem(int position) {
        return conferences.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView dateStart;

        public TextView location;

        public TextView headline;

        public TextView speaker;

        public ImageView image;

        public ImageView favorite;

        public View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }
}
