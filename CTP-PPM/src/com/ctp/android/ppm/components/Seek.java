package com.ctp.android.ppm.components;

import com.ctp.android.ppm.R;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Seek extends Activity {
    /** Called when the activity is first created. */
	 AudioManager audioManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider_layout);
        final TextView reading = (TextView) findViewById(R.id.readingSlider);
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekbarSlider);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				reading.setText("Processing "+progress+"% ");
			}
		});
        }
}