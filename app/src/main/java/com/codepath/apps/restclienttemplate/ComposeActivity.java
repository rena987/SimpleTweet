package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 280;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityComposeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_compose);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        client = TwitterApp.getRestClient(this);

        if (getIntent().hasExtra("reply")) {
            Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
            binding.tvRepliedName.setText("@" + tweet.getUser().getScreenName());
            binding.etCompose.setText(binding.tvRepliedName.getText().toString() + " ");
        }

        // Set click listener on button
        binding.btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweet = binding.etCompose.getText().toString();
                if (tweet.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweet.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long!", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, tweet, Toast.LENGTH_LONG).show();
                // Make an API Call to Twitter to publish the tweet
                client.postTweet(tweet, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.e(TAG, "onSuccess to post tweet");
                        JSONObject jsonObject = json.jsonObject;
                        try {
                            Tweet tweety = Tweet.fromJson(jsonObject);
                            Log.i(TAG, "Published Tweet: " + tweety);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweety));
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to post tweet: " + throwable);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true; // must return true in order for menu to be displayed
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.closeCompose) {
            // Compose icon has been selected
            Toast.makeText(this, "Close!", Toast.LENGTH_SHORT).show();
            // Navigate to the compose activity
            Intent intent = new Intent(this, TimelineActivity.class);
            startActivity(intent);
            return true; // false to allow normal menu processing to proceed but true to consume it here
        }
        return super.onOptionsItemSelected(item);
    }
}