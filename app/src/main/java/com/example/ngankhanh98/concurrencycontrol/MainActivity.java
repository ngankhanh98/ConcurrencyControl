package com.example.ngankhanh98.concurrencycontrol;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    ProgressBar myBarHorizontal;
    TextView lblPercent;
    EditText txtDataBox;
    Button btnDoItAgain;

    int progressStep = 1;
    int MAX_PROGRESS;
    int accum = 0;
    int globalVar = 0;

    boolean isRunning = false;
    Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtDataBox = (EditText) (findViewById(R.id.editText));
        myBarHorizontal = (ProgressBar) (findViewById(R.id.progressBar));
        btnDoItAgain = (Button) (findViewById(R.id.button));
        lblPercent = (TextView) (findViewById(R.id.txtShowPercent));
        lblPercent = (TextView)(findViewById(R.id.txtShowPercent));


        btnDoItAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MAX_PROGRESS = Integer.parseInt(txtDataBox.getText().toString());

                btnDoItAgain.setEnabled(false);
                onStart();
            }// onClick
        });// setOnClickListener
    }

    @Override
    protected void onStart() {
        super.onStart();
        // prepare UI components

//
                // reset and show progress bInteger.parseInt(txtDataBox.getText().toString());ars
                accum = 0;
        myBarHorizontal.setMax(MAX_PROGRESS);
        myBarHorizontal.setProgress(0);
        myBarHorizontal.setVisibility(View.VISIBLE);
        // create-start background thread were the busy work will be done

        Thread myBackgroundThread = new Thread(backgroundTask, "backAlias1");
        myBackgroundThread.start();

    }

    private Runnable foregroundRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                // update UI, observe globalVar is changed in back thread
                // advance ProgressBar
                myBarHorizontal.incrementProgressBy(progressStep);
                accum += progressStep;
                lblPercent.setText(Integer.toString(myBarHorizontal.getProgress()*100/MAX_PROGRESS)+"%");
                // are we done yet?
                if (accum >= myBarHorizontal.getMax()) {
                    //myBarHorizontal.setVisibility(View.INVISIBLE);
                    btnDoItAgain.setEnabled(true);
                }
            } catch (Exception e) {
                Log.e("<<foregroundTask>>", e.getMessage());
            }
        }
    }; // foregroundTask


    private Runnable backgroundTask = new Runnable() {
        @Override
        public void run() {
            // busy work goes here...
            try {
                for (int n = 0; n < MAX_PROGRESS; n++) {
                    // this simulates 1 sec. of busy activity
                    Thread.sleep(0);
                    // change a global variable here...
                    globalVar++;
                    // try: next two UI operations should NOT work
                    // Toast.makeText(getApplication(), "Hi ", 1).show();
                    // txtDataBox.setText("Hi ");
                    // wake up foregroundRunnable delegate to speak for you
                    myHandler.post(foregroundRunnable);
                }
            } catch (InterruptedException e) {
                Log.e("<<foregroundTask>>", e.getMessage());
            }
        }
    };
}