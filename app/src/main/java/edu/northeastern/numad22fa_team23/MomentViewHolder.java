package edu.northeastern.numad22fa_team23;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad22fa_team23.model.Comment;

public class MomentViewHolder extends RecyclerView.ViewHolder {

    TextView userName;
    TextView musicName;
    TextView thought;
    View view;
    Button addComment;
    int momentID;
    RecyclerView recyclerView;
    CommentAdapter commentAdapter;
    LinearLayoutManager linearLayoutManager;

    public void showComment(List<Comment> commentList) {
        commentAdapter.setData(commentList);
        recyclerView.setAdapter(commentAdapter);
    }

    MomentViewHolder(Context context, View itemView, final MomentsAdapter.OnItemClickListener onClickListener) {
        super(itemView);
        userName = (TextView) itemView.findViewById(R.id.userName);
        musicName = (TextView) itemView.findViewById(R.id.musicName);
        thought = (TextView) itemView.findViewById(R.id.thought);
        addComment = (Button) itemView.findViewById(R.id.addComment);
        recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerViewComment);
        commentAdapter = new CommentAdapter(context);
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onClickListener.onButtonClicked(view, position, momentID);
                    }
                }
            }
        });
        view = itemView;
    }
}
