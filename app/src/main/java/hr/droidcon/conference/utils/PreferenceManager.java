package hr.droidcon.conference.utils;

import android.content.SharedPreferences;
import com.tale.prettysharedpreferences.BooleanEditor;
import com.tale.prettysharedpreferences.LongEditor;
import com.tale.prettysharedpreferences.PrettySharedPreferences;

/**
 * A basic implementation of {@link com.tale.prettysharedpreferences.PrettySharedPreferences}
 * @author Arnaud Camus
 */
public class PreferenceManager extends PrettySharedPreferences<PreferenceManager> {

    public PreferenceManager(SharedPreferences sharedPreferences) {
        super(sharedPreferences);
    }

    public BooleanEditor<PreferenceManager> favorite(String title) {
        return getBooleanEditor(title);
    }

    public BooleanEditor<PreferenceManager> favoritesChanged() {
        return getBooleanEditor("favoritesChanged");
    }

    public LongEditor<PreferenceManager> openingApp() {
        return getLongEditor("nbOpening");
    }
}
