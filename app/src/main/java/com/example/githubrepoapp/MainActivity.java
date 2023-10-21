package com.example.githubrepoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.example.githubrepoapp.ApiInterface.GitHubApi;
import com.example.githubrepoapp.adapter.IssueAdapter;
import com.example.githubrepoapp.databinding.ActivityMainBinding;
import com.example.githubrepoapp.model.Issue;

import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private IssueAdapter adapter;
    private GitHubApi gitHubApi;

    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IssueAdapter();
        binding.recyclerView.setAdapter(adapter);

        searchView = binding.searchView;

        // Create an instance of OkHttpClient to add the authorization header
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request originalRequest = chain.request();
                    Request newRequest = originalRequest.newBuilder()
                            .addHeader("Accept", "application/vnd.github+json")
                            .addHeader("Authorization", "Bearer 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")  // Replace YOUR-TOKEN with your actual token
                            .addHeader("X-GitHub-Api-Version", "2022-11-28")
                            .build();
                    return chain.proceed(newRequest);
                })
                .build();

        // Use the OkHttpClient with Retrofit
        Retrofit authenticatedRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        gitHubApi = authenticatedRetrofit.create(GitHubApi.class);


        binding.searchView.setQueryHint("Search Issues");

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    // If the search query is empty, clear the adapter
                    adapter.setIssues(null);
                } else {
                    searchClosedIssues();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    // If the search query is cleared, clear the adapter
                    adapter.setIssues(null);
                }
                return true;
            }
        });
    }

    private void searchClosedIssues() {
        Call<List<Issue>> call = gitHubApi.getClosedIssues("closed", "");
        call.enqueue(new Callback<List<Issue>>() {
            @Override
            public void onResponse(@NonNull Call<List<Issue>> call, @NonNull Response<List<Issue>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Issue> allIssues = response.body();

                    // Get the user's search query from the searchView
                    String query = searchView.getQuery().toString().toLowerCase();

                    // Filter issues based on the keyword(s) in the title
                    List<Issue> filteredIssues = new ArrayList<>();
                    for (Issue issue : allIssues) {
                        if (issue.getTitle().toLowerCase().contains(query)) {
                            filteredIssues.add(issue);
                        }
                    }

                    // Update the adapter with the filtered issues
                    adapter.setIssues(filteredIssues);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Issue>> call, @NonNull Throwable t) {
                Log.e("Closed", "Search request failed: " + t.getMessage());
            }
        });
    }


}
