package com.gabilheri.githubviewer.network;

import com.gabilheri.githubviewer.data.Owner;
import com.gabilheri.githubviewer.data.Repo;
import com.gabilheri.githubviewer.data.UserToken;

import java.util.ArrayList;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by <a href="mailto:marcusandreog@gmail.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 11/22/14.
 */
public class GithubClient {

    public static final String API_URL = "https://api.github.com";

    public static class Contributor {
        public String login;
        public int contributions;
    }


    public static class Auth {
        public String token;
    }

    public interface GithubAuth {
        @POST("/authorizations")
        UserToken getUserToken(@Body LoginRequest body);
    }

    public interface GithubOwner {
        @GET("/user")
        Owner getOwner();
    }

    public interface GitHubContributors {
        @GET("/repos/{owner}/{repo}/contributors")
        List<Contributor> contributors(
                @Path("owner") String owner,
                @Path("repo") String repo
        );
    }

    public interface GithubRepos {
        @GET("/users/{owner}/repos")
        ArrayList<Repo> getRepos(
            @Path("owner") String owner
        );
    }

    public static RestAdapter getBaseRestAdapter(RequestInterceptor interceptor) {

        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setEndpoint(API_URL);

        if(interceptor != null) {
            builder.setRequestInterceptor(interceptor);
        }

        return builder.build();
    }

}