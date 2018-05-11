package hforh.oranges.aslrecognizer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DashboardWelcomeScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_welcome_screen);
        Intent intent = new Intent(DashboardWelcomeScreen.this, ASLDisplayActivity.class);
        startActivity(intent);
    }

    public void cameraButtonClick(View view) {
         // send to OCR Capture activity
    }
}
