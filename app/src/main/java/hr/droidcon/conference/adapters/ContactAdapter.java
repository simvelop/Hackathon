package hr.droidcon.conference.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import hr.droidcon.conference.R;
import hr.droidcon.conference.timeline.Link;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private OnLinkClickListener onLinkClickListener;
    private List<Link> links = new ArrayList<>();

    public ContactAdapter(List<Link> links) {
        this.links = links;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

//        colorItem = context.getResources().getColor(R.color.menu_item);
//        colorIcon = context.getResources().getColor(R.color.menu_icon);
        return new ViewHolder(inflater.inflate(R.layout.item_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.name.setText(links.get(position).getTitle());
        holder.url.setText(links.get(position).getUrl());

    }

    @Override
    public int getItemCount() {
        return links.size();
    }

    public void setOnLinkClickListener(OnLinkClickListener onLinkClickListener) {
        this.onLinkClickListener = onLinkClickListener;
    }

    public void onItemClick(int position) {
        if (onLinkClickListener != null)
            onLinkClickListener.onLinkCLick(links.get(position));
    }

    public interface OnLinkClickListener {
        void onLinkCLick(Link link);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView name;
        public final TextView url;

        private final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(getAdapterPosition());
            }
        };

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            url = (TextView) itemView.findViewById(R.id.url);

            itemView.setOnClickListener(onClickListener);
        }
    }
}
