package hforh.oranges.aslrecognizer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class EditTextActivity extends Activity {

    public String textToTranslate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        getTextFromIntent();
        populateText();
    }

    protected void populateText() {
        EditText initialText = (EditText) findViewById(R.id.textBox);
        initialText.setText(textToTranslate);
        ((Button)findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((Button)findViewById(R.id.doneEditButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editedText = (EditText) findViewById(R.id.textBox);
                textToTranslate = editedText.getText().toString(); // feed this into translator
                Intent intent = new Intent(EditTextActivity.this, MainActivity.class);
                intent.putExtra("textToTranslate", textToTranslate);
                startActivity(intent);
                finish();
            }
        });
    }

    public void getTextFromIntent() {
        Intent intent = getIntent();
        textToTranslate = intent.getStringExtra("textToTranslate");
    }
}
