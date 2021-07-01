package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailsActivity extends AppCompatActivity {

    ActivityDetailsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        Glide.with(this).load(tweet.getUser().getProfileImageUrl()).into(binding.dtProfilePic);
        binding.dtScreenName.setText("@" + tweet.getUser().getScreenName());
        binding.dtName.setText(tweet.getUser().getName());
        binding.dtBody.setText(tweet.getBody());

        if (tweet.isFavorited()) {
            binding.dtLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_heart));
        }

        if (tweet.isRetweeted()) {
            binding.dtRetweet.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_retweet));
        }

        binding.dtReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.dtReply.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_messages));
                Intent intent = new Intent(DetailsActivity.this, ComposeActivity.class);
                intent.putExtra("reply", true);
                intent.putExtra("tweet", Parcels.wrap(tweet));
                startActivity(intent);
            }
        });

        binding.dtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LikeOrUnlikeTweet(tweet);
            }
        });

        binding.dtRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetweetOrUnRetweet(tweet);
            }
        });
    }

    public void LikeOrUnlikeTweet(Tweet tweet) {
        TwitterClient client = TwitterApp.getRestClient(this);
        if (tweet.favorited) {
            client.UnlikeTweet(tweet.id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    binding.dtLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_heart_stroke));
                    Toast.makeText(DetailsActivity.this, "Unliked!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                }
            });

        } else {
            client.LikeTweet(tweet.id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    binding.dtLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_heart));
                    Toast.makeText(DetailsActivity.this, "Liked!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e("DetailsActivity", "Error liking tweet: " + throwable);
                }
            });
        }
        tweet.favorited = !tweet.favorited;
    }

    public void RetweetOrUnRetweet(Tweet tweet) {
        TwitterClient client = TwitterApp.getRestClient(this);
        if (tweet.retweeted) {
            client.postUnRetweet(tweet.id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    binding.dtRetweet.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_retweet_stroke));
                    Toast.makeText(DetailsActivity.this, "Unretweeted!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e("DetailsActivity", "Error retweeting: " + throwable);
                }
            });

        } else {
            client.postRetweet(tweet.id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    binding.dtRetweet.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_retweet));
                    Toast.makeText(DetailsActivity.this, "Retweeted!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e("DetailsActivity", "Error retweeting: " + throwable);
                }
            });
        }
        tweet.retweeted = !tweet.retweeted;

    }


}