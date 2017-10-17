package edu.orangecoastcollege.cs273.flagquiz;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * An activity for the settings menu
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * Called when the activity is starting.
     * Shows the back to home button in top right corner
     * Uses a {@link android.app.FragmentManager} to populate the list with {@link SettingsActivityFragment}
     *
     * @param savedInstanceState  If the activity is being re-initialized after previously being shut down then
     *                            this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsActivityFragment()).commit();
    }

    /**
     * Shows preferences as a list
     */
    public static  class SettingsActivityFragment extends PreferenceFragment {
        /**
         * Loads up the preferences from R.xml.preferences
         *
         * @param savedInstanceState Previous initialization data
         */
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

        }
    }
}
