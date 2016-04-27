package hr.droidcon.conference.utils;

import android.content.SharedPreferences;

import com.tale.prettysharedpreferences.BooleanEditor;
import com.tale.prettysharedpreferences.LongEditor;
import com.tale.prettysharedpreferences.PrettySharedPreferences;
import com.tale.prettysharedpreferences.StringEditor;

/**
 * A basic implementation of {@link com.tale.prettysharedpreferences.PrettySharedPreferences}
 * @author Arnaud Camus
 */
public class PreferenceManager  extends PrettySharedPreferences {

    public PreferenceManager(SharedPreferences sharedPreferences) {
        super(sharedPreferences);
    }

    public BooleanEditor<PreferenceManager> favorite(String title) {
        return getBooleanEditor(title);
    }

    public StringEditor<PreferenceManager> schedule(String startDate) {
        return getStringEditor(startDate);
    }

    public LongEditor<PreferenceManager> openingApp() {
        return getLongEditor("nbOpening");
    }
}