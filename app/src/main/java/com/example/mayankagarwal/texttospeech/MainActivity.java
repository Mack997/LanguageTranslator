package com.example.mayankagarwal.texttospeech;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    String english;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);


        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    english = txtSpeechInput.getText().toString();
                    GoogleTranslate googleTranslate = new GoogleTranslate();

                    try {
                        String hindi = googleTranslate.execute(english, "en", "hi").get();
                        txtSpeechInput.setText(hindi);
                        System.out.print(hindi);
                    } catch (InterruptedException|ExecutionException e) {
                        Log.e("Google Response ", e.getMessage());
                    }

                }
                break;
            }

        }
    }

    public class GoogleTranslate extends AsyncTask<String, Void, String> {

        private final String API_KEY = "AIzaSyAazeQmo8s87l2jTuLvccTCbep4M5o1hM8";


        @Override
        protected String doInBackground(String... params){

            final String textToTranslate = params[0];
            final String SOURCE_LANGUAGE = params[1];
            final String TARGET_LANGUAGE = params[2];

            try {

                NetHttpTransport netHttpTransport = new NetHttpTransport();

                JacksonFactory jacksonFactory = new JacksonFactory();

                Translate translate = new Translate.Builder(netHttpTransport, jacksonFactory, null).build();

                Translate.Translations.List listToTranslate = translate.new Translations().list(
                        Arrays.asList(textToTranslate), TARGET_LANGUAGE).setKey(API_KEY);

                listToTranslate.setSource(SOURCE_LANGUAGE);

                TranslationsListResponse response = listToTranslate.execute();

                return response.getTranslations().get(0).getTranslatedText();

            }catch (Exception e){

                Log.e("Google Response ", e.getMessage());

                return "";
            }
        }
    }
}
