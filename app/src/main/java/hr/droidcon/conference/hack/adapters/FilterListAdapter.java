package hr.droidcon.conference.hack.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hr.droidcon.conference.hack.BaseApplication;
import hr.droidcon.conference.hack.R;
import hr.droidcon.conference.hack.objects.Conference;
import hr.droidcon.conference.hack.utils.WordColor;

/**
 * Created by stefan.tanovic on 4/27/2016.
 */
public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.ViewHolder> {

    public static final int COUNT_VIEWS = 2;

    public static final int VIEW_HEADER = 0;
    public static final int VIEW_CONFERENCE = 1;
    private final Context context;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
    private List<Conference> conferences;
//    private ViewHeaderInflater viewHeaderInflater;
//    private ViewConferenceInflater viewConferenceInflater;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        ViewHolder holder;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conference, parent, false);

            holder = new ViewHolder(v);
            holder.dateStart = (TextView) v.findViewById(R.id.dateStart);
            holder.location = (TextView) v.findViewById(R.id.location);
            holder.headline = (TextView) v.findViewById(R.id.headline);
            holder.speaker = (TextView) v.findViewById(R.id.speaker);
            holder.image = (ImageView) v.findViewById(R.id.image);
            holder.favorite = (ImageView) v.findViewById(R.id.favorite);
            return holder;


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Conference object = getItem(position);
        try {
            Date startDate = dateFormat.parse(object.getStartDate());
            holder.dateStart.setText(simpleDateFormat.format(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.location.setText(String.format(context.getString(R.string.location),
                object.getLocation()));
        holder.location.setTextColor(WordColor.generateColor(object.getLocation()));
        holder.headline.setText(Html.fromHtml(object.getHeadline()));
        holder.speaker.setText(Html.fromHtml(object.getSpeaker()));

        // picasso
        Picasso.with(context.getApplicationContext())
                .load(object.getSpeakerImageUrl())
                .transform(((BaseApplication) context.getApplicationContext()).mPicassoTransformation)
                .into(holder.image);
        holder.favorite.setImageResource(object.isFavorite(context)
                ? R.drawable.ic_favorite_grey600_18dp
                : R.drawable.ic_favorite_outline_grey600_18dp);

    }

    @Override
    public int getItemCount() {
        return conferences.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView dateStart;
        public TextView location;
        public TextView headline;
        public TextView speaker;
        public ImageView image;
        public ImageView favorite;

        public ViewHolder(View view) {
            super(view);
        }
    }

    public FilterListAdapter(Context context, int resource, List<Conference> objects) {
        this.context = context;
        conferences = objects;
//        conferences = new ArrayList<>();
//        conferences.add(new Conference(new String[]{"sdf","SDF","sdfs ","sdfs","sdfsd","SDFsd","sdfsdf"}));
//        conferences.add(new Conference(new String[]{"sdf","SDF","sdfs ","sdfs","sdfsd","SDFsd","sdfsdf"}));
//
//        conferences.add(new Conference(new String[]{"sdf","SDF","sdfs ","sdfs","sdfsd","SDFsd","sdfsdf"}));
//
//        conferences.add(new Conference(new String[]{"sdf","SDF","sdfs ","sdfs","sdfsd","SDFsd","sdfsdf"}));
//
//        conferences.add(new Conference(new String[]{"sdf","SDF","sdfs ","sdfs","sdfsd","SDFsd","sdfsdf"}));
//
//        conferences.add(new Conference(new String[]{"sdf","SDF","sdfs ","sdfs","sdfsd","SDFsd","sdfsdf"}));

//        viewHeaderInflater = new ViewHeaderInflater(context);
//        viewConferenceInflater = new ViewConferenceInflater(context);
    }

    public Conference getItem(int position) {
        return conferences.get(position);
    }
}
