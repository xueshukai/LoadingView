package xsk.cn.progressview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    private LoadingView1 progressView1;
    int i = 10;
    float s = 0.02f;
    private LoadingView2 lv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressView1 = (LoadingView1) findViewById(R.id.progress1);
        lv2 = (LoadingView2) findViewById(R.id.lv2);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(800);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    lv2.setPointDiffT(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar.setProgress(200);
    }

    public void onClick(View view) {
        progressView1.setRadius(i++);
    }

    public void onWidth(View view) {
        progressView1.setThinWidthPercent(s , 0.4f);
        s+=0.1;
    }

}
