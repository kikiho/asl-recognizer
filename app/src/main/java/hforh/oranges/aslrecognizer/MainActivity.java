package hforh.oranges.aslrecognizer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private String textToTranslate;
    private ArrayList<SignedWordVideo> videos = new ArrayList<>();
    private static int wordCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getTextToTranslateFromIntent();
        ((Button)findViewById(R.id.typetextbutton)).setVisibility(View.INVISIBLE);
        ((Button)findViewById(R.id.typetextbutton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditTextActivity.class);
                intent.putExtra("textToTranslate", textToTranslate);
                startActivity(intent);
                finish();
            }
        });

        ((Button)findViewById(R.id.gobackbutton)).setVisibility(View.INVISIBLE);
        ((Button)findViewById(R.id.gobackbutton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new GetHTML().execute();
    }

    public void getTextToTranslateFromIntent() {
        Intent intent = getIntent();
        textToTranslate = intent.getStringExtra("textToTranslate");
    }

    public class GetHTML extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            for (String word : textToTranslate.split(" ")) {
                Log.d("ocr", word);
                String urlLink = "http://www.signasl.org/sign/" + word;
                URL url;
                try {
                    url = new URL(urlLink);
                    Log.d("ocr", "Opening url connection " + urlLink);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    BufferedReader httpInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer stringBuffer = new StringBuffer();
                    String inputLine, video = "";
                    while ((inputLine = httpInput.readLine()) != null) {
                        if (inputLine.contains(".mp4")) {
                            continue;
                            /*video = inputLine.substring(inputLine.lastIndexOf("content=") + 8);
                            video = video.replace("\"", "");
                            Log.d("OCR", video.toString());
                            stringBuffer.append(video);
                            break;*/
                        }
                        else if ((inputLine.contains("youtube"))){
                            video = inputLine.substring((inputLine.lastIndexOf("src=") + 8));
                            video = video.replace("\"", "");
                            Log.d("OCR", video.toString());
                            stringBuffer.append(video);
                            break;
                        }
                    }
                    if (video == "") {
                        Log.d("OCR", "Sorry, no video found, try again");
                    }
                    videos.add(new SignedWordVideo(video, word));
                    Log.d("ocr", "Closing connection");
                    httpInput.close();


                } catch (Exception e) {
                    Log.e("ocr", "Could not get string from URL.");
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (videos.isEmpty() || ifNoURLSFound()) {
                ((TextView) findViewById(R.id.statusTextBox)).setText("Sorry, no videos found :(");
                ((Button)findViewById(R.id.gobackbutton)).setVisibility(View.VISIBLE);
                ((Button)findViewById(R.id.typetextbutton)).setVisibility(View.VISIBLE);
            }
            else {
                startAppropriateActivity();
                finish();
            }
        }
    }

    private boolean ifNoURLSFound() {
        for(SignedWordVideo video : videos){
            if (!video.getVideoLink().isEmpty()){
                return false;
            }
        }
        return true;
    }

    private void startAppropriateActivity() {
        Intent intent = new Intent(MainActivity.this, ASLDisplayActivity.class);
        for(SignedWordVideo video : videos){
            Log.d("AAAAAA", "Putting linnk and word: " + video.getVideoLink() + video.getWordName());
            intent.putExtra("link" + wordCount, video.getVideoLink());
            intent.putExtra("word" + wordCount, video.getWordName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            wordCount++;
        }
        intent.putExtra("numberOfWords", wordCount);
        wordCount = 0;
        startActivity(intent);
    }
}