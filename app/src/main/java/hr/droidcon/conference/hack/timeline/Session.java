package hr.droidcon.conference.hack.timeline;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hrvoje Kozak on 29/03/16.
 */
public class Session {

    @SerializedName("title")
    private String title;
    @SerializedName("speaker_names")
    private List<String> speakerNames;
    @SerializedName("speaker_uids")
    private List<String> speakerUIDs;

    public List<String> getSpeakerUIDs() {
        return speakerUIDs;
    }

    public void setSpeakerUIDs(List<String> speakerUIDs) {
        this.speakerUIDs = speakerUIDs;
    }

    @SerializedName("uri")
    private String uri;

    private List<String> room;

    @SerializedName("abstract")
    private String abstractHTML;

    @SerializedName("start_iso")
    private List<String> startISO;

    @SerializedName("end_iso")
    private List<String> endISO;

    @SerializedName("category")
    private Object category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSpeakerNames() {
        return speakerNames;
    }

    public void setSpeakerNames(List<String> speakerNames) {
        this.speakerNames = speakerNames;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<String> getRoom() {
        return room;
    }

    public void setRoom(List<String> room) {
        this.room = room;
    }

    public String getAbstractHTML() {
        // remove strong tags
        return abstractHTML.replaceAll("</?strong>", "");
    }

    public void setAbstractHTML(String abstractHTML) {
        this.abstractHTML = abstractHTML;
    }

    public List<String> getStartISO() {
        return startISO;
    }

    public void setStartISO(List<String> startISO) {
        this.startISO = startISO;
    }

    public List<String> getEndISO() {
        return endISO;
    }

    public void setEndISO(List<String> endISO) {
        this.endISO = endISO;
    }

    public Object getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
