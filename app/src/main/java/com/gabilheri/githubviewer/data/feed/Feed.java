package com.gabilheri.githubviewer.data.feed;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by <a href="mailto:marcusandreog@gmail.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 11/23/14.
 */

public class Feed extends SugarRecord<Feed> {

    @SerializedName("type")
    private String type;

    @SerializedName("actor")
    private FeedActor feedActor;

    @SerializedName("repo")
    private FeedRepo feedRepo;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("payload")
    private Payload payload;

    public String getType() {
        return type;
    }

    public FeedActor getFeedActor() {
        return feedActor;
    }

    public FeedRepo getFeedRepo() {
        return feedRepo;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Payload getPayload() {
        return payload;
    }

    public Feed() {

    }


    @Override
    public String toString() {
        return "Feed{" +
                "type='" + type + '\'' +
                ", feedActor=" + feedActor +
                ", feedRepo=" + feedRepo +
                ", createdAt=" + createdAt +
                ", payload=" + payload +
                '}';
    }
}