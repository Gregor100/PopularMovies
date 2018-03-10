package com.udacity.gregor.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DetailActivity extends AppCompatActivity {

    @InjectView(R.id.iv_poster_detail)
    ImageView detailPoster;

    @InjectView(R.id.tv_overview)
    TextView overview;

    @InjectView(R.id.tv_vote_count)
    TextView voteCount;

    @InjectView(R.id.tv_release_date)
    TextView releaseDate;

    @InjectView(R.id.tv_original_title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent mainToDetailIntent = getIntent();
        if(mainToDetailIntent.hasExtra(Intent.EXTRA_TEXT)){
            String[] intentData = mainToDetailIntent.getStringArrayExtra(Intent.EXTRA_TEXT);
            String titleString = intentData[MainActivity.titlePosition];
            String overviewString = intentData[MainActivity.synopsisPosition];
            String voteCountString = intentData[MainActivity.voteCountPosition];
            String posterPathString = intentData[MainActivity.posterPathPosition];
            String releaseDateString = intentData[MainActivity.releaseDatePosition];
            title.setText(titleString);
            overview.setText(overviewString);
            voteCount.setText(voteCountString);
            releaseDate.setText(releaseDateString);
            Picasso.with(this)
                    .load(MainActivity.BASE_URL + MainActivity.SIZE_POSTER +
                            posterPathString)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(detailPoster);
        }else{
            Log.i("Info", "No intent extra strings");
        }
    }
}
