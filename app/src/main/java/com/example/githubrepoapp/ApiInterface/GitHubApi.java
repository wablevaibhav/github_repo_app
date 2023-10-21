package com.example.githubrepoapp.ApiInterface;

import com.example.githubrepoapp.model.Issue;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GitHubApi {
    @GET("repos/flutter/flutter/issues")
    Call<List<Issue>> getClosedIssues(
            @Query("state") String state,
            @Query("q") String query
    );
}

