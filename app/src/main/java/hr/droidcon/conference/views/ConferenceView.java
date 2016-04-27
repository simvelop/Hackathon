package hr.droidcon.conference.views;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import hr.droidcon.conference.BaseApplication;
import hr.droidcon.conference.hack.R;
import hr.droidcon.conference.objects.Conference;
import hr.droidcon.conference.utils.WordColor;

/**
 * Created by admund on 2016-04-27.
 */
public class ConferenceView {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, HH:mm");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

    public static View createView(Context context, Conference conference) {

        View v = LayoutInflater.from(context).inflate(R.layout.conference_view, null, false);

        TextView dateStart = (TextView) v.findViewById(R.id.dateStart);
//        TextView location = (TextView) v.findViewById(R.id.location);
        TextView headline = (TextView) v.findViewById(R.id.headline);
        TextView speaker = (TextView) v.findViewById(R.id.speaker);
        ImageView image = (ImageView) v.findViewById(R.id.image);
        ImageView favorite = (ImageView) v.findViewById(R.id.favorite);


        try {
            Date startDate = dateFormat.parse(conference.getStartDate());
            dateStart.setText(simpleDateFormat.format(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        location.setText(String.format(context.getString(R.string.location),
//                conference.getLocation()));
//        location.setTextColor(WordColor.generateColor(conference.getLocation()));
        headline.setText(Html.fromHtml(conference.getHeadline()));
        speaker.setText(Html.fromHtml(conference.getSpeaker()));

        // picasso
        Picasso.with(context.getApplicationContext())
                .load(conference.getSpeakerImageUrl())
                .transform(((BaseApplication) context.getApplicationContext()).mPicassoTransformation)
                .into(image);

        favorite.setImageResource(conference.isFavorite(context)
                ? R.drawable.ic_favorite_grey600_18dp
                : R.drawable.ic_favorite_outline_grey600_18dp);
        return v;
    }

    public static View getBlankView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.blank_conference_view, null, false);
    }
}
