package hr.droidcon.conference.hack;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import hr.droidcon.conference.hack.timeline.Speaker;
import hr.droidcon.conference.hack.timeline.TimelineAPI;
import hr.droidcon.conference.hack.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TimeKillGame extends Activity {

    private TextView moveCounter;
    private TextView feedbackText;
    private Button[] buttons;
    private static final Integer[] goal = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    private ArrayList<Integer> cells = new ArrayList<>();


    private List<Speaker> mSpeakers;
    private Bitmap[] bitmapsArray = new Bitmap[9];

    @Bind(R.id.game_container) RelativeLayout gameContainer;
    @Bind(R.id.start_frame) FrameLayout startFrame;
    @Bind(R.id.start_message) TextView startMessage;
    @Bind(R.id.restart) TextView restartButton;

    private Speaker speaker;

    /**
     * Enable to share views across activities with animation on Android 5.0 Lollipop
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupLollipop() {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new ChangeBounds());
        getWindow().setSharedElementEnterTransition(new ChangeBounds());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Utils.isLollipop()) {
            setupLollipop();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_kill);

        ButterKnife.bind(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setTitle(getString(R.string.action_kill_time));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        gameContainer.setVisibility(View.GONE);

        fetchSpeakers();

    }

    private void setGame(){

        buttons = findButtons();

        for (int i = 0; i < 9; i++) {
            cells.add(i);
        }
        Collections.shuffle(cells); //random cells array

        fill_grid();


        moveCounter = (TextView) findViewById(R.id.MoveCounter);
        feedbackText = (TextView) findViewById(R.id.FeedbackText);

        for (int i = 1; i < 9; i++) {
            buttons[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    makeMove((Button) v);
                }
            });
        }

        moveCounter.setText("0");
        feedbackText.setText("Waiting for your move");

        startFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameContainer.setVisibility(View.VISIBLE);
                startFrame.setVisibility(View.GONE);
            }
        });

    }
    private void setSpeaker(){
        int size = mSpeakers.size();
        speaker = mSpeakers.get(new Random().nextInt(size));

        final ImageView ivOne = (ImageView) findViewById(R.id.one);

        Picasso.with(getApplicationContext())
                .load(speaker.getImage())
                .centerCrop()
                .resize(300, 300)
                .into(ivOne, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        startMessage.setText(String.format("Click to start \n compiling \n %s %s", speaker.getFirstName(), speaker.getLastName()));
                        Bitmap originalBm = ((BitmapDrawable) ivOne.getDrawable()).getBitmap();
                        createImageArrays(originalBm);
                        setGame();
                    }
                    @Override
                    public void onError() {
                        startMessage.setText("failed to download image");
                    }
                });
    }

    private void fetchSpeakers() {
        Log.d("KILL TIME", "fetch speaker called");
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.SIMVELOP_ENDPOINT)
                .build();

        TimelineAPI timelineAPI = retrofit.create(TimelineAPI.class);

        Call<List<Speaker>> getSpeakers = timelineAPI.getSpeakers();

        getSpeakers.enqueue(new Callback<List<Speaker>>() {
            @Override
            public void onResponse(Call<List<Speaker>> call, Response<List<Speaker>> response) {

                if (response.isSuccessful()) {
                    mSpeakers = response.body();
                    setSpeaker();
                }
            }

            @Override
            public void onFailure(Call<List<Speaker>> call, Throwable t) {
                Toast.makeText(TimeKillGame.this, "No internet connection, sorry :(",
                        Toast.LENGTH_SHORT)
                        .show();
                startMessage.setText("bug, sorry");
            }
        });
    }

    void createImageArrays(Bitmap bMap) {
        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 240, 240, true);

        bitmapsArray[0] = Bitmap.createBitmap(bMapScaled, 0, 0, 80, 80);
        bitmapsArray[1] = Bitmap.createBitmap(bMapScaled, 80, 0, 80, 80);
        bitmapsArray[2] = Bitmap.createBitmap(bMapScaled, 160, 0, 80, 80);
        bitmapsArray[3] = Bitmap.createBitmap(bMapScaled, 0, 80, 80, 80);
        bitmapsArray[4] = Bitmap.createBitmap(bMapScaled, 80, 80, 80, 80);
        bitmapsArray[5] = Bitmap.createBitmap(bMapScaled, 160, 80, 80, 80);
        bitmapsArray[6] = Bitmap.createBitmap(bMapScaled, 0, 160, 80, 80);
        bitmapsArray[7] = Bitmap.createBitmap(bMapScaled, 80, 160, 80, 80);
        bitmapsArray[8] = Bitmap.createBitmap(bMapScaled, 160, 160, 80, 80);

        findViewById(R.id.Button00).setBackground(new BitmapDrawable(bitmapsArray[0]));
        findViewById(R.id.Button00).setVisibility(View.INVISIBLE);
        findViewById(R.id.Button01).setBackground(new BitmapDrawable(bitmapsArray[1]));
        findViewById(R.id.Button02).setBackground(new BitmapDrawable(bitmapsArray[2]));
        findViewById(R.id.Button03).setBackground(new BitmapDrawable(bitmapsArray[3]));
        findViewById(R.id.Button04).setBackground(new BitmapDrawable(bitmapsArray[4]));
        findViewById(R.id.Button05).setBackground(new BitmapDrawable(bitmapsArray[5]));
        findViewById(R.id.Button06).setBackground(new BitmapDrawable(bitmapsArray[6]));
        findViewById(R.id.Button07).setBackground(new BitmapDrawable(bitmapsArray[7]));
        findViewById(R.id.Button08).setBackground(new BitmapDrawable(bitmapsArray[8]));

    }

    public Button[] findButtons() {
        Button[] b = new Button[9];

        b[0] = (Button) findViewById(R.id.Button00);
        b[1] = (Button) findViewById(R.id.Button01);
        b[2] = (Button) findViewById(R.id.Button02);
        b[3] = (Button) findViewById(R.id.Button03);
        b[4] = (Button) findViewById(R.id.Button04);
        b[5] = (Button) findViewById(R.id.Button05);
        b[6] = (Button) findViewById(R.id.Button06);
        b[7] = (Button) findViewById(R.id.Button07);
        b[8] = (Button) findViewById(R.id.Button08);
        return b;
    }

    public void makeMove(final Button b) {
        Boolean bad_move = true;
        int b_text, b_pos, zuk_pos;
        b_text = Integer.parseInt((String) b.getText());
        b_pos = find_pos(b_text);
        zuk_pos = find_pos(0);
        switch (zuk_pos) {
            case (0):
                if (b_pos == 1 || b_pos == 3)
                    bad_move = false;
                break;
            case (1):
                if (b_pos == 0 || b_pos == 2 || b_pos == 4)
                    bad_move = false;
                break;
            case (2):
                if (b_pos == 1 || b_pos == 5)
                    bad_move = false;
                break;
            case (3):
                if (b_pos == 0 || b_pos == 4 || b_pos == 6)
                    bad_move = false;
                break;
            case (4):
                if (b_pos == 1 || b_pos == 3 || b_pos == 5 || b_pos == 7)
                    bad_move = false;
                break;
            case (5):
                if (b_pos == 2 || b_pos == 4 || b_pos == 8)
                    bad_move = false;
                break;
            case (6):
                if (b_pos == 3 || b_pos == 7)
                    bad_move = false;
                break;
            case (7):
                if (b_pos == 4 || b_pos == 6 || b_pos == 8)
                    bad_move = false;
                break;
            case (8):
                if (b_pos == 5 || b_pos == 7)
                    bad_move = false;
                break;
        }

        if (bad_move) {
            feedbackText.setText("Move Not Allowed");
            return;
        }
        feedbackText.setText("Move OK");
        cells.remove(b_pos);
        cells.add(b_pos, 0);
        cells.remove(zuk_pos);
        cells.add(zuk_pos, b_text);


        fill_grid();
        moveCounter.setText(Integer.toString(Integer.parseInt((String) moveCounter.getText()) + 1));

        for (int i = 0; i < 9; i++) {
            if (cells.get(i) != goal[i]) {
                Log.d("WINNING", "cell " + cells.get(i) + " = " + goal[i] + "goal");
                return;
            }
        }
        feedbackText.setText("Well done!");
        findViewById(R.id.Button00).setVisibility(View.VISIBLE);
        restartButton.setVisibility(View.VISIBLE);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpeaker();
            }
        });

    }

    public void fill_grid() {
        for (int i = 0; i < 9; i++) {
            int text = cells.get(i);
            AbsoluteLayout.LayoutParams absParams =
                    (AbsoluteLayout.LayoutParams) buttons[text].getLayoutParams();

            final float scale = getResources().getDisplayMetrics().density;
            int px = (int) (0* scale + 0.5f);
            switch (i) {
                case (0):
                    absParams.x = px;
                    absParams.y = px;
                    buttons[text].setLayoutParams(absParams);
                    break;
                case (1):

                    absParams.x = (int) (100 * scale + 0.5f);
                    absParams.y = (int) (0* scale + 0.5f);
                    buttons[text].setLayoutParams(absParams);
                    break;
                case (2):

                    absParams.x = (int) (200 * scale + 0.5f);
                    absParams.y = (int) (0* scale + 0.5f);
                    buttons[text].setLayoutParams(absParams);
                    break;
                case (3):

                    absParams.x = (int) (0* scale + 0.5f);
                    absParams.y = (int) (100 * scale + 0.5f);
                    buttons[text].setLayoutParams(absParams);
                    break;
                case (4):

                    absParams.x = (int) (100 * scale + 0.5f);
                    absParams.y = (int) (100 * scale + 0.5f);
                    buttons[text].setLayoutParams(absParams);
                    break;
                case (5):

                    absParams.x = (int) (200 * scale + 0.5f);
                    absParams.y = (int) (100 * scale + 0.5f);
                    buttons[text].setLayoutParams(absParams);
                    break;
                case (6):

                    absParams.x = (int) (0* scale + 0.5f);
                    absParams.y = (int) (200 * scale + 0.5f);
                    buttons[text].setLayoutParams(absParams);
                    break;
                case (7):

                    absParams.x = (int) (100 * scale + 0.5f);
                    absParams.y = (int) (200 * scale + 0.5f);
                    buttons[text].setLayoutParams(absParams);
                    break;
                case (8):

                    absParams.x = (int) (200 * scale + 0.5f);
                    absParams.y = (int) (200 * scale + 0.5f);
                    buttons[text].setLayoutParams(absParams);
                    break;

            }


        }

    }

    public int find_pos(int element) {
        int i;
        for (i = 0; i < 9; i++) {
            if (cells.get(i) == element) {
                break;
            }
        }
        return i;
    }
}
