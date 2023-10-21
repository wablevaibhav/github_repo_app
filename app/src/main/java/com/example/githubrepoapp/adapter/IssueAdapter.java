package com.example.githubrepoapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.githubrepoapp.databinding.IssueItemBinding;
import com.example.githubrepoapp.model.Issue;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.ViewHolder> {
    private List<Issue> issues;

    public IssueAdapter() {

    }

    // Create a setter method for the issues
    @SuppressLint("NotifyDataSetChanged")
    public void setIssues(List<Issue> issues) {
        this.issues = issues;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        IssueItemBinding itemBinding = IssueItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Issue issue = issues.get(position);

        holder.itemBinding.titleTextView.setText(issue.getTitle());
        holder.itemBinding.createdDateTextView.setText( "Created At: " +formatDate(issue.getCreated_at()));
        holder.itemBinding.closedDateTextView.setText("Closed At: " + formatDate(issue.getClosed_at()));
        holder.itemBinding.userNameTextView.setText("User Name: " + issue.getUser().getLogin());

        // Load the user image using Picasso
        String userImageUrl = issue.getUser().getAvatar_url();
        if (userImageUrl != null) {
            Picasso.get().load(userImageUrl).into(holder.itemBinding.userImageView);
        }

    }

    // Helper method to format the date as "dd-MM-yy"
    private String formatDate(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            Date parsedDate = inputFormat.parse(date);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yy", Locale.US);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return date; // Return the original date if there's an error
        }
    }

    @Override
    public int getItemCount() {

        if (issues != null) {
            return issues.size();
        } else {
            return 0;
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final IssueItemBinding itemBinding;

        public ViewHolder(IssueItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
    }
}

