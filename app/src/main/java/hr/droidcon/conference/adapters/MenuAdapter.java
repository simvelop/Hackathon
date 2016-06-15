package hr.droidcon.conference.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import hr.droidcon.conference.R;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private OnMenuElementClickListener onMenuElementClickListener;
    private int colorItem;
    private int colorIcon;
    private Context context;

    public MenuAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        colorItem = context.getResources().getColor(R.color.menu_item);
        colorIcon = context.getResources().getColor(R.color.menu_icon);
        return new ViewHolder(inflater.inflate(R.layout.item_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MenuElement menuElement = menuElements.get(position);

        holder.name.setText(menuElement.getName());
        holder.icon.setImageResource(menuElement.getIcon());
        holder.name.setTextColor(colorItem);
        holder.icon.setColorFilter(colorIcon);

    }

    @Override
    public int getItemCount() {
        return menuElements.size();
    }

    public void setOnMenuElementClickListener(OnMenuElementClickListener onMenuElementClickListener) {
        this.onMenuElementClickListener = onMenuElementClickListener;
    }

    public void onItemClick(int position) {
        if (onMenuElementClickListener != null)
            onMenuElementClickListener.onMenuCLick(menuElements.get(position));
    }

    public interface OnMenuElementClickListener {
        void onMenuCLick(MenuElement menuElement);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView name;
        public final ImageView icon;

        private final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(getAdapterPosition());
            }
        };

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            icon = (ImageView) itemView.findViewById(R.id.icon);

            itemView.setOnClickListener(onClickListener);
        }
    }

    public class MenuElement {
        private int icon;
        private String name;

        public MenuElement(int icon, String name) {
            this.icon = icon;
            this.name = name;
        }

        public int getIcon() {
            return icon;
        }

        public String getName() {
            return name;
        }
    }

    public ArrayList<MenuElement> menuElements = new ArrayList<MenuElement>(){{
        add(new MenuElement(R.drawable.ic_menu_speakers, "speakers"));
        add(new MenuElement(R.drawable.ic_floating_fb, "facebook"));
        add(new MenuElement(R.drawable.ic_menu_instagram, "instagram"));
        add(new MenuElement(R.drawable.ic_menu_twitter, "twitter"));
        add(new MenuElement(R.drawable.ic_menu_info, "about"));
    }};
}
