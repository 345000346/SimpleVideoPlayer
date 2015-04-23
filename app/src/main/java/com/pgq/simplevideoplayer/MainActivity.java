package com.pgq.simplevideoplayer;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private String path = "http://live.3gv.ifeng.com/zixun.m3u8";
    private static final String MEDIA = "media";
    private static final int LOCAL_VIDEO = 4;
    private static final int STREAM_VIDEO = 5;

    private EditText pathText;
    private Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pathText = (EditText) findViewById(R.id.et_path);
        playButton = (Button) findViewById(R.id.btn_play);
        pathText.setText(path);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path = pathText.getText().toString().trim();

                Intent intent = new Intent(getApplicationContext(), VideoViewPlayer.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
    }
}
