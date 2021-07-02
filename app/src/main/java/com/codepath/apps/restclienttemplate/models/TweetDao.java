package com.codepath.apps.restclienttemplate.models;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TweetDao {

    @Query("SELECT Tweet.body AS tweet_body, Tweet.createdAt AS tweet_createdAt, Tweet.mediaUrl AS tweet_mediaUrl, Tweet.numOfLikes AS tweet_numOfLikes, Tweet.numOfRetweets AS tweet_numOfRetweets, Tweet.favorited AS tweet_favorited, Tweet.retweeted AS tweet_retweeted, User.* " +
            "FROM Tweet INNER JOIN User ON Tweet.user_id = User.id ORDER BY createdAt DESC LIMIT 5")
    List<TweetWithUser> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);

}
