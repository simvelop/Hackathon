package hr.droidcon.conference.events;

/**
 * Created by christian on 27.04.16.
 */
public class FilterUpdateEvent {

    boolean showOnlyFavorites;

    public FilterUpdateEvent(boolean showOnlyFavorites){
        this.showOnlyFavorites = showOnlyFavorites;
    }

    public boolean isShowOnlyFavorites() {
        return showOnlyFavorites;
    }
}
