package hr.droidcon.conference;

import android.util.SparseArray;

import hr.droidcon.conference.timeline.Speaker;

/**
 * Created by
 * @author Mateusz Kłodnicki
 * on 27.04.16.
 */
public enum SpeakersManager {
    INSTANCE;

    private SparseArray<Speaker> speakers = new SparseArray<>();

    public SparseArray<Speaker> getSpeakers() {
        return speakers;
    }


}
