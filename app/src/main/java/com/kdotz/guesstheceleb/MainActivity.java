package com.kdotz.guesstheceleb;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList urlList = new ArrayList();
    ArrayList nameList = new ArrayList();
    ImageView imageView;
    String[] answers = new String[4];
    int locationOfCorrectAnswer = 0;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button1 = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);

        DownloadTask task = new DownloadTask();
        String result = null;

        try {
            result = task.execute("http://posh24.se/kandisar").get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Pattern p = Pattern.compile("img src=\"(.*?)\"");
        Matcher m = p.matcher(result);

        while (m.find()) {
            if (!m.group(1).contains(":list"))
                urlList.add(m.group(1));
        }

        p = Pattern.compile("alt=\"(.*?)\"");
        m = p.matcher(result);

        while (m.find()) {
            nameList.add(m.group(1));
        }

        newQuestion();

        Log.i("Result", result);
    }

    public void newQuestion(){
        Random random = new Random();
        counter = random.nextInt(urlList.size());
        downloadImage(imageView);

        locationOfCorrectAnswer = random.nextInt(4);
        int incorrectAnswerLocation;

        for (int i = 0; i < 4; i++) {
            if (i == locationOfCorrectAnswer) {
                answers[i] = String.valueOf(nameList.get(counter));
            } else {
                incorrectAnswerLocation = random.nextInt(urlList.size());
                while (incorrectAnswerLocation == counter) {
                    incorrectAnswerLocation = random.nextInt(counter);
                }
                answers[i] = String.valueOf(nameList.get(incorrectAnswerLocation));
            }
        }

        button1.setText(answers[0]);
        button2.setText(answers[1]);
        button3.setText(answers[2]);
        button4.setText(answers[3]);
    }

    public void downloadImage(View view) {
        ImageDownloader task = new ImageDownloader();
        Bitmap myImage;
        try {
            myImage = task.execute(String.valueOf(urlList.get(counter))).get();
            imageView.setImageBitmap(myImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void celebChosen(View view) {
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "False! It is " + nameList.get(counter), Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }
}
