package com.egormoroz.schooly.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.chat.Message;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommentFragment extends Fragment {

    private FirebaseModel firebaseModel;
    private CommentAdapter commentAdapter;
    private RecyclerView commentList;
    private String newsID, commentSenderName;
    private ImageButton sendComment;
    private EditText inputText;
    private DatabaseReference RootRef;
    UserInformation userInformation;
    Bundle bundle;

    private final List<Comment> commentsArray = new ArrayList<>();

    public CommentFragment(UserInformation userInformation, Bundle bundle) {
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static CommentFragment newInstance(UserInformation userInformation, Bundle bundle) {
        return new CommentFragment(userInformation,bundle);

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_comments, container, false);
        Bundle data = this.getArguments();
        if (data != null) {
            newsID = data.getString("newsID");
        }

        commentList = root.findViewById(R.id.comments_list);
        sendComment = root.findViewById(R.id.send_comment_btn);
        inputText = root.findViewById(R.id.input_message);

        initControls();
        return root;
    }

    @Override
    public void onStart() {

        super.onStart();
        RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
            @Override
            public void PassUserNick(String nick) {
                firebaseModel.getReference("news").child(newsID).child("comments")
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                Comment comment = dataSnapshot.getValue(Comment.class);
                                commentsArray.add(comment);
                                commentAdapter.notifyDataSetChanged();
                                commentList.smoothScrollToPosition(commentList.getAdapter().getItemCount());
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = inputText.getText().toString();

                if (TextUtils.isEmpty(comment)) {
                    Toast.makeText(getContext(), "first write your comment...", Toast.LENGTH_SHORT).show();
                } else {
                    String commentRef = "news/" + newsID + "/comments";


                    DatabaseReference commentKeyRef = firebaseModel.getReference("news").child(newsID).child("comments").push();
                    String commentID = commentKeyRef.getKey();
                    Map<String, String> commentTextBody = new HashMap<String, String>();
                    commentTextBody.put("comment", comment);
                    commentTextBody.put("likesCount", "0");
                    commentTextBody.put("nick", userInformation.getNick());
                    commentTextBody.put("postTime", RecentMethods.getCurrentTime());
                    commentTextBody.put("commentID", commentID);

                    Map<String, Object> messageBodyDetails = new HashMap<>();
                    messageBodyDetails.put(commentRef + "/" + commentID, commentKeyRef);
                    RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            inputText.setText("");
                        }
                    });
                }
            }
        });
    }


    public void initControls(){

        RootRef = firebaseModel.getReference("news");
        //commentAdapter = new CommentAdapter(commentsArray);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        commentList.setLayoutManager(linearLayoutManager);
        commentList.setAdapter(commentAdapter);
        commentList.setItemViewCacheSize(20);
    }
}
