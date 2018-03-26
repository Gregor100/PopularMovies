package com.udacity.gregor.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

/**
 * Created by Gregor on 24.02.2018.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_most_popular:
                if(checked){
                    MainActivity.MOST_POPULAR = true;
                    MainActivity.BEST_RATED = false;
                    MainActivity.SHOW_FAVORITES = false;
                }
                break;
            case R.id.radio_best_rated:
                if(checked){
                    MainActivity.BEST_RATED = true;
                    MainActivity.MOST_POPULAR = false;
                    MainActivity.SHOW_FAVORITES = false;
                }
                break;

            case R.id.radio_favorites:
                if (checked) {
                    MainActivity.SHOW_FAVORITES = true;
                    MainActivity.MOST_POPULAR = false;
                    MainActivity.BEST_RATED = false;
                }
                break;
        }

        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
