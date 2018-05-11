package hforh.oranges.aslrecognizer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ASLDisplayActivity extends YouTubeFailureRecoveryActivity implements
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        YouTubePlayer.OnFullscreenListener{
    private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9
            ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

    private LinearLayout baseLayout;
    private YouTubePlayerView playerView;
    private YouTubePlayer player;
    private View otherViews;
    private ArrayList<SignedWordYoutubeID> youtubeVideoIDs = new ArrayList<>();

    private boolean fullscreen;
    private int numberOfVideos;
    private int currentlyPlayingVideoIndex;
    private ArrayList<String> listOfUnfoundWords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asldisplay);
        baseLayout = (LinearLayout) findViewById(R.id.layout);
        playerView = (YouTubePlayerView) findViewById(R.id.player);
        ((Button)findViewById(R.id.backButtton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        otherViews = findViewById(R.id.other_views);

        playerView.initialize(DeveloperKey.DEVELOPER_KEY, this);

        getYoutubeVideoIDFromIntent();
        doLayout();
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return playerView;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        this.player = youTubePlayer;
        // Specify that we want to handle fullscreen behavior ourselves.
        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        player.setOnFullscreenListener(this);
        player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {

            }

            @Override
            public void onAdStarted() {

            }

            @Override
            public void onVideoStarted() {

            }

            @Override
            public void onVideoEnded() {
                if (currentlyPlayingVideoIndex < youtubeVideoIDs.size() - 1) {
                    currentlyPlayingVideoIndex++;
                }
                else if (youtubeVideoIDs.size() != 1){
                    currentlyPlayingVideoIndex = currentlyPlayingVideoIndex % (youtubeVideoIDs.size() - 1);
                }
                player.loadVideo(youtubeVideoIDs.get(currentlyPlayingVideoIndex).getVideoID());
                ((TextView)findViewById(R.id.videonametextview))
                        .setText(youtubeVideoIDs.get(currentlyPlayingVideoIndex).getWordName());
            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        });
        if (!wasRestored) {
            player.loadVideo(youtubeVideoIDs.get(currentlyPlayingVideoIndex).getVideoID());
            ((TextView)findViewById(R.id.videonametextview))
                    .setText(youtubeVideoIDs.get(currentlyPlayingVideoIndex).getWordName());
            ((TextView)findViewById(R.id.notfoundtextview)).setText(getUnfoundWordsString());
        }
    }

    private void doLayout() {
        LinearLayout.LayoutParams playerParams =
                (LinearLayout.LayoutParams) playerView.getLayoutParams();
        if (fullscreen) {
            // When in fullscreen, the visibility of all other views than the player should be set to
            // GONE and the player should be laid out across the whole screen.
            playerParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = LinearLayout.LayoutParams.MATCH_PARENT;

            otherViews.setVisibility(View.GONE);
        } else {
            // This layout is up to you - this is just a simple example (vertically stacked boxes in
            // portrait, horizontally stacked in landscape).
            otherViews.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams otherViewsParams = otherViews.getLayoutParams();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                playerParams.width = otherViewsParams.width = 0;
                playerParams.height = WRAP_CONTENT;
                otherViewsParams.height = MATCH_PARENT;
                playerParams.weight = 1;
                baseLayout.setOrientation(LinearLayout.HORIZONTAL);
            } else {
                playerParams.width = otherViewsParams.width = MATCH_PARENT;
                playerParams.height = WRAP_CONTENT;
                playerParams.weight = 0;
                otherViewsParams.height = 0;
                baseLayout.setOrientation(LinearLayout.VERTICAL);
            }
        }
    }

    @Override
    public void onClick(View v) {
        player.setFullscreen(!fullscreen);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        /*int controlFlags = player.getFullscreenControlFlags();
        if (isChecked) {
            // If you use the FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE, your activity's normal UI
            // should never be laid out in landscape mode (since the video will be fullscreen whenever the
            // activity is in landscape orientation). Therefore you should set the activity's requested
            // orientation to portrait. Typically you would do this in your AndroidManifest.xml, we do it
            // programmatically here since this activity demos fullscreen behavior both with and without
            // this flag).
            setRequestedOrientation(PORTRAIT_ORIENTATION);
            controlFlags |= YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            controlFlags &= ~YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
        }
        player.setFullscreenControlFlags(controlFlags);*/
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        fullscreen = isFullscreen;
        doLayout();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        doLayout();
    }

    public void getYoutubeVideoIDFromIntent() {
        Intent intent = getIntent();
        int numberOfWords = intent.getIntExtra("numberOfWords", 0);
        numberOfVideos = numberOfWords;
        for (int i = 0; i < numberOfWords; i++){
            String url = intent.getStringExtra("link" + i);
            String wordName = intent.getStringExtra("word" + i);
            if (!url.isEmpty()) {
                String[] urlSplit1 = url.split(Pattern.quote("embed/"));
                String[] urlSplit2 = urlSplit1[1].split(Pattern.quote("?"));
                SignedWordYoutubeID youtubeVideoID = new SignedWordYoutubeID(urlSplit2[0], wordName);
                Log.d("AAAAAA", youtubeVideoID.getVideoID());
                youtubeVideoIDs.add(youtubeVideoID);
            }
            else {
                Log.d("AAAAAA", "UNFOUND WORD: " + wordName);
                listOfUnfoundWords.add(wordName);
            }
        }
    }

    public String  getUnfoundWordsString() {
        if (listOfUnfoundWords.size() == 0){
            return "";
        }
        String words = "Could not find words: ";
        for(String word : listOfUnfoundWords){
            words += "[" + word + "] ";
        }
        Log.d("AAAAADAS" , "Returning unfound string: " + words);
        return words;
    }


}
