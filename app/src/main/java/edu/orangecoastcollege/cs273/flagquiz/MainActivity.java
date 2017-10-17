package edu.orangecoastcollege.cs273.flagquiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Flag Quiz";

    private static final int FLAGS_IN_QUIZ = 10;

    private Button[] mButtons = new Button[8];
    private LinearLayout[] mLayouts = new LinearLayout[4];
    private List<Country> mAllCountriesList;  // all the countries loaded from JSON
    private List<Country> mQuizCountriesList; // countries in current quiz (just 10 of them)
    private List<Country> mFilteredCountriesList;
    private Country mCorrectCountry; // correct country for the current question
    private int mTotalGuesses; // number of total guesses made
    private int mCorrectGuesses; // number of correct guesses
    private SecureRandom rng; // used to randomize the quiz
    private Handler handler; // used to delay loading next country

    private TextView mQuestionNumberTextView; // shows current question #
    private ImageView mFlagImageView; // displays a flag
    private TextView mAnswerTextView; // displays correct answer

    private int mChoices;
    private String mRegion;

    public static final String CHOICES = "pref_numberOfChoices";
    public static final String REGIONS = "pref_regions";

    /**
     * Called on the creation of the activity
     * Loads any existing states of the activity from savedInstanceState
     * Connects Views to their respective programmed variables
     * Instantiates the {@link ArrayList}, {@link SecureRandom}, and {@link Handler}
     * Loads a list ofd all the possible countries into mAllCountriesList using a {@link JSONLoader}
     * calls resetQuiz()
     *
     * @param savedInstanceState Any previous run of the activity and its data set
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);

        mQuizCountriesList = new ArrayList<>(FLAGS_IN_QUIZ);
        rng = new SecureRandom();
        handler = new Handler();

        // TODO: Get references to GUI components (textviews and imageview)
        mQuestionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);
        mFlagImageView = (ImageView) findViewById(R.id.flagImageView);
        mAnswerTextView = (TextView) findViewById(R.id.answerTextView);
        // TODO: Put all 4 buttons in the array (mButtons)
        mButtons[0] = (Button) findViewById(R.id.button);
        mButtons[1] = (Button) findViewById(R.id.button2);
        mButtons[2] = (Button) findViewById(R.id.button3);
        mButtons[3] = (Button) findViewById(R.id.button4);
        mButtons[4] = (Button) findViewById(R.id.button5);
        mButtons[5] = (Button) findViewById(R.id.button6);
        mButtons[6] = (Button) findViewById(R.id.button7);
        mButtons[7] = (Button) findViewById(R.id.button8);

        mLayouts[0] = (LinearLayout) findViewById(R.id.row1LinearLayout);
        mLayouts[1] = (LinearLayout) findViewById(R.id.row2LinearLayout);
        mLayouts[2] = (LinearLayout) findViewById(R.id.row3LinearLayout);
        mLayouts[3] = (LinearLayout) findViewById(R.id.row4LinearLayout);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mRegion = preferences.getString(REGIONS, "All");
        mChoices = Integer.parseInt(
                preferences.getString(CHOICES, "4"));
        updateChoices();


        // TODO: Set mQuestionNumberTextView's text to the appropriate strings.xml resource
        mQuestionNumberTextView.setText(getString(R.string.question, 0, FLAGS_IN_QUIZ));
        // TODO: Load all the countries from the JSON file using the JSONLoader
        try {
            mAllCountriesList = JSONLoader.loadJSONFromAsset(this);

            updateRegion();
        // TODO: Call the method resetQuiz() to start the quiz.
            resetQuiz();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up and starts a new quiz.
     */
    public void resetQuiz() {
        // TODO: Reset the number of correct guesses made
        mCorrectGuesses = 0;
        // TODO: Reset the total number of guesses the user made
        mTotalGuesses = 0;
        // TODO: Clear list of quiz countries (for prior games played)
        mQuizCountriesList.clear();

        // TODO: Randomly add FLAGS_IN_QUIZ (10) countries from the mAllCountriesList into the mQuizCountriesList
        // TODO: Ensure no duplicate countries (e.g. don't add a country if it's already in mQuizCountriesList)
        boolean notAdded = true;
        for (int i = 0; i < FLAGS_IN_QUIZ; i++) {
            notAdded = true;
            while(notAdded) {
                Country country = mFilteredCountriesList.get(rng.nextInt(mFilteredCountriesList.size()));
                if (!mQuizCountriesList.contains(country)) {
                    mQuizCountriesList.add(country);
                    notAdded = false;
                }
            }
        }

        // TODO: Start the quiz by calling loadNextFlag
        loadNextFlag();
    }

    /**
     * Method initiates the process of loading the next flag for the quiz, showing
     * the flag's image and then 4 buttons, one of which contains the correct answer.
     */
    private void loadNextFlag() {
        // TODO: Initialize the mCorrectCountry by removing the item at position 0 in the mQuizCountries
        mCorrectCountry = mQuizCountriesList.remove(0);
        // TODO: Clear the mAnswerTextView so that it doesn't show text from the previous question
        mAnswerTextView.setText("");
        // TODO: Display current question number in the mQuestionNumberTextView
        mQuestionNumberTextView.setText(getString(R.string.question, mCorrectGuesses + 1, FLAGS_IN_QUIZ));

        // TODO: Use AssetManager to load next image from assets folder
        AssetManager am = getAssets();

        // TODO: Get an InputStream to the asset representing the next flag
        try {
            InputStream inputStream = am.open(mCorrectCountry.getFileName());
        // TODO: and try to use the InputStream to create a Drawable
        // TODO: The file name can be retrieved from the correct country's file name.
            Drawable flag = Drawable.createFromStream(inputStream, mCorrectCountry.getName());
        // TODO: Set the image drawable to the correct flag.
            mFlagImageView.setImageDrawable(flag);
        // TODO: Shuffle the order of all the countries (use Collections.shuffle)
            Collections.shuffle(mFilteredCountriesList);
        // TODO: Loop through all 4 buttons, enable them all and set them to the first 4 countries
        // TODO: in the all countries list
            while(mFilteredCountriesList.subList(0,mButtons.length).contains(mCorrectCountry)) Collections.shuffle(mFilteredCountriesList);
            for (int i = 0; i < mChoices; i++) {
                mButtons[i].setEnabled(true);

                mButtons[i].setText(mFilteredCountriesList.get(i).getName());
            }

        // TODO: After the loop, randomly replace one of the 4 buttons with the name of the correct country
            mButtons[rng.nextInt(mChoices)].setText(mCorrectCountry.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the click event of one of the 4 buttons indicating the guess of a country's name
     * to match the flag image displayed.  If the guess is correct, the country's name (in GREEN) will be shown,
     * followed by a slight delay of 2 seconds, then the next flag will be loaded.  Otherwise, the
     * word "Incorrect Guess" will be shown in RED and the button will be disabled.
     * @param v
     */
    public void makeGuess(View v) {
        // TODO: Downcast the View v into a Button (since it's one of the 4 buttons)
        Button guessedCountry = (Button) v;
        // TODO: Get the country's name from the text of the button
        String countryName = guessedCountry.getText().toString();
        // TODO: If the guess matches the correct country's name, increment the number of correct guesses,
        // TODO: then display correct answer in green text.  Also, disable all 4 buttons (can't keep guessing once it's correct)
        if(countryName.equals(mCorrectCountry.getName())) {
            mCorrectGuesses++;
            mTotalGuesses++;
            mAnswerTextView.setTextColor(ContextCompat.getColor(this, R.color.correct_answer));
            mAnswerTextView.setText(countryName);
            for (Button mButton : mButtons) {
                mButton.setEnabled(false);
            }
            // TODO: Nested in this decision, if the user has completed all 10 questions, show an AlertDialog
            // TODO: with the statistics and an option to Reset Quiz
            if(mCorrectGuesses == 10) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                Log.i(TAG, "Correct guesses: " + mCorrectGuesses + ", Total Guesses: " + mTotalGuesses + ", Answer: " + ((float) (mCorrectGuesses) / (float)(mTotalGuesses)));
                builder.setMessage(getString(R.string.results, mTotalGuesses, 100.0 * ((float) (mCorrectGuesses) / (float)(mTotalGuesses))));
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.reset_quiz, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetQuiz();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        loadNextFlag();
                    }
                };
                handler.postDelayed(runnable, 1200);
            }
        }

        // TODO: Else, the answer is incorrect, so display "Incorrect Guess!" in red
        // TODO: and disable just the incorrect button.
        else {
            mAnswerTextView.setTextColor(ContextCompat.getColor(this, R.color.incorrect_answer));
            mAnswerTextView.setText(getString(R.string.incorrect_answer));
            guessedCountry.setEnabled(false);
            mTotalGuesses++;
        }
    }

    /**
     * Called on the creation of the options menu
     * Inflates the created view
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed; if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return  super.onCreateOptionsMenu(menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal processing happen
     * (calling the item's Runnable or sending a message to its Handler as appropriate).
     * Launches a content to {@link SettingsActivity}
     *
     * @param item The menu item that was selected.
     * @return Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
        return super.onOptionsItemSelected(item);
    }

    /**
     * A callback to be invoked when a shared preference is changed.
     */
    SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        /**
         * Called when a shared preference is changed, added, or removed. This may be called even if a preference is set to its existing value.
         *
         * @param sharedPreferences The SharedPreferences that received the change.
         * @param key The key of the preference that was changed, added, or removed.
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case CHOICES:
                    mChoices = Integer.parseInt(sharedPreferences.getString(CHOICES, "4"));
                    updateChoices();
                    resetQuiz();
                    break;
                case REGIONS:
                    mRegion = sharedPreferences.getString(REGIONS, "All");
                    updateRegion();
                    resetQuiz();
                    Log.e(TAG, "REGION CHANGED");
                    break;
            }
            Log.e(TAG, "MAKING TOAST");
            Toast.makeText(MainActivity.this, R.string.restarting_quiz, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Updates the amount of question in the quiz
     */
    private void updateChoices() {
        for (int i = 0; i < mLayouts.length; i++) {
            if (i < mChoices / 2) {
                mLayouts[i].setEnabled(true);
                mLayouts[i].setVisibility(View.VISIBLE);
            }
            else {
                mLayouts[i].setEnabled(false);
                mLayouts[i].setVisibility(View.GONE);
            }
        }
    }

    /**
     * Updates the region which the quiz's countries are chosen from
     */
    private void updateRegion() {
        if (mRegion.equals("All")) {
            mFilteredCountriesList = new ArrayList<>(mAllCountriesList);
        } else {
            mFilteredCountriesList = new ArrayList<>();
            for (Country country : mAllCountriesList) {
                if(country.getRegion().equals(mRegion))
                    mFilteredCountriesList.add(country);
            }
        }
    }
}
