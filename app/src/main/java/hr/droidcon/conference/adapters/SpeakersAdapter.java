package hr.droidcon.conference.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hr.droidcon.conference.BaseApplication;
import hr.droidcon.conference.hack.R;
import hr.droidcon.conference.timeline.Speaker;

public class SpeakersAdapter extends RecyclerView.Adapter<SpeakersAdapter.ViewHolder> {

    private OnSpeakerClickListener onSpeakerClickListener;
    private List<Speaker> speakers = new ArrayList<>();
    private Context context;

    public SpeakersAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        return new ViewHolder(inflater.inflate(R.layout.item_speaker, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Speaker speaker = speakers.get(position);

        holder.name.setText(speaker.getFirstName() + " " + speaker.getLastName());
        holder.job.setText(speaker.getCompanyPosition() + " at " + speaker.getCompany());

        Picasso.with(context.getApplicationContext())
                .load(speaker.getImage())
                .transform(((BaseApplication) context.getApplicationContext()).mPicassoTransformation)
                .into(holder.image);

    }

    public void setItems(@Nullable List<Speaker> speakers) {
        if (speakers == null) {
            this.speakers = Collections.emptyList();
        } else {
            this.speakers = speakers;
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return speakers.size();
    }

    public interface OnSpeakerClickListener {
        void onSpeakerClick(Speaker speaker);
    }

    public void setOnSpeakerClickListener(OnSpeakerClickListener onSpeakerClickListener) {
        this.onSpeakerClickListener = onSpeakerClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView image;
        public final TextView name;
        public final TextView job;

        private final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSpeakerClickListener != null) {
                    onSpeakerClickListener.onSpeakerClick(speakers.get(getAdapterPosition()));
                }
            }
        };

        public ViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.speakerImage);
            name = (TextView) itemView.findViewById(R.id.speakerName);
            job = (TextView) itemView.findViewById(R.id.speakerJob);

            itemView.setOnClickListener(onClickListener);
        }
    }
}
