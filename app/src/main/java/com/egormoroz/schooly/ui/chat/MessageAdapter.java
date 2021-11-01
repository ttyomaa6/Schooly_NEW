package com.egormoroz.schooly.ui.chat;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.CONST;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.ui.chat.holders.ImageViewerActivity;
import com.egormoroz.schooly.ui.main.ChatActivity;
import com.egormoroz.schooly.ui.main.ChatsFragment;
import com.egormoroz.schooly.ui.main.GroupChatActivity;
import com.egormoroz.schooly.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> userMessagesList;
    private DatabaseReference usersRef;
    private String messageSenderId = "", messageReceiverId = "";
    private MediaPlayer player;
    private String fromUserID;
    private String messageID;
    private DatabaseReference ref;

    public MessageAdapter(List<Message> userMessagesList, String messageSenderId, String messageReceiverId ) {
        this.userMessagesList = userMessagesList;
        this.messageSenderId = messageSenderId;
        this.messageReceiverId = messageReceiverId;
    }

    public MessageAdapter(List<Message> userMessagesList, String messageSenderId) {
        this.userMessagesList = userMessagesList;
        this.messageSenderId = messageSenderId;
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receiverMessageText, senderMessageTime, receiverMessageTime;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture;
        public ImageView messageReceiverPicture;
        public static ImageView messageSenderPlay;
        public static ImageView messageReceiverPlay;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverMessageTime = itemView.findViewById(R.id.receiver_time);
            senderMessageTime = itemView.findViewById(R.id.sender_time);
            messageReceiverPlay = itemView.findViewById(R.id.sender_voice);
            messageSenderPlay = itemView.findViewById(R.id.receiver_voice);
            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout, viewGroup, false);
        ref = FirebaseDatabase.getInstance().getReference().child("users");

        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, @SuppressLint("RecyclerView") final int position) {

        Message messages = userMessagesList.get(position);

        boolean Start = true;
        fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")) {
                    String receiverImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.ic_image).into(messageViewHolder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//

        messageViewHolder.receiverMessageText.setVisibility(View.GONE);
        messageViewHolder.receiverProfileImage.setVisibility(View.GONE);
        messageViewHolder.senderMessageText.setVisibility(View.GONE);
        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);
        messageViewHolder.messageSenderPlay.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPlay.setVisibility(View.GONE);

        if (fromMessageType.equals("text")) {
            if (fromUserID.equals(messageSenderId)) {
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);

                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.cornerstextmessagesfromme);
                messageViewHolder.senderMessageText.setTextColor(Color.WHITE);
                messageViewHolder.senderMessageText.setText(messages.getMessage());
                messageViewHolder.senderMessageTime.setText(messages.getTime());
            } else {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);

                messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.cornerstextmessagesfromother);
                messageViewHolder.receiverMessageText.setTextColor(Color.WHITE);
                messageViewHolder.receiverMessageText.setText(messages.getMessage());
                messageViewHolder.receiverMessageTime.setText(messages.getTime());
            }
        } else if (fromMessageType.equals("image")) {
            if (fromUserID.equals(messageSenderId)) {
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);

            } else {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);
            }
        } else if (fromMessageType.equals("pdf") || fromMessageType.equals("docx")) {
            if (fromUserID.equals(messageSenderId)) {
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                messageViewHolder.messageSenderPicture.setBackgroundResource(R.drawable.cornerstextmessagesfromme);
                messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        messageViewHolder.itemView.getContext().startActivity(intent);
                    }
                });

            } else {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                messageViewHolder.messageSenderPicture.setBackgroundResource(R.drawable.cornerstextmessagesfromother);
            }
        } else if (fromMessageType.equals("voice")) {
            if (fromUserID.equals(messageSenderId)) {
                MessageViewHolder.messageSenderPlay.setVisibility(View.VISIBLE);
                MessageViewHolder.messageSenderPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPlay(Start);
                    }
                });
            }
        } else {
            MessageViewHolder.messageReceiverPlay.setVisibility(View.VISIBLE);
            MessageViewHolder.messageReceiverPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPlay(Start);
                }
            });

        }

        if (fromUserID.equals(messageSenderId)) {
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx")) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete For me",
                                        "Download and View this Document",
                                        "Cancel",
                                        "Delete For EveryOne"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                if (pos == 0) {
                                    messageID = userMessagesList.get(position).getMessageID();
                                    deleteSentMessage(position);

                                } else if (pos == 1) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                } else if (pos == 3) {
                                    messageID = userMessagesList.get(position).getMessageID();
                                    deleteMessageForEveryOne(position, messageViewHolder);
                                    delete(position);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("text")) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete For me",
                                        "Cancel",
                                        "Delete For EveryOne"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                if (pos == 0) {

                                   // messageID = userMessagesList.get(position).getMessageID();
                                    usersRef.child("Messages")
                                            .child(userMessagesList.get(position).getMessageID())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                delete(position);
                                            }
                                            else
                                            {
                                                delete(position);
                                            }
                                        }
                                    });
                                    //deleteSentMessage(position);

                                } else if (pos == 2) {
                                    messageID = userMessagesList.get(position).getMessageID();
                                    deleteMessageForEveryOne(position, messageViewHolder);
                                    delete(position);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("image")) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete For me",
                                        "View This Image",
                                        "Cancel",
                                        "Delete For EveryOne"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                if (pos == 0) {
                                    messageID = userMessagesList.get(position).getMessageID();
                                    deleteSentMessage(position);
                                    delete(position);

                                } else if (pos == 1) {
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), ImageViewerActivity.class);
                                    intent.putExtra("url", userMessagesList.get(position).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                } else if (pos == 3) {
                                    messageID = userMessagesList.get(position).getMessageID();
                                    deleteMessageForEveryOne(position, messageViewHolder);
                                    delete(position);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        } else {
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx")) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete For me",
                                        "Download and View this Document",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                if (pos == 0) {
                                    messageID = userMessagesList.get(position).getMessageID();
                                    deleteReceiverMessage(position, messageViewHolder);
                                    delete(position);
                                } else if (pos == 1) {
                                    messageID = userMessagesList.get(position).getMessageID();
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("text")) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete For me",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                if (pos == 0) {
                                    messageID = userMessagesList.get(position).getMessageID();
                                    deleteReceiverMessage(position, messageViewHolder);
                                    delete(position);
//                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), ChatActivity.class);
//                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("image")) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete For me",
                                        "View This Image",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                if (pos == 0) {
                                    messageID = userMessagesList.get(position).getMessageID();
                                    deleteReceiverMessage(position, messageViewHolder);
                                    delete(position);

                                } else if (pos == 1) {
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), ImageViewerActivity.class);
                                    intent.putExtra("url", userMessagesList.get(position).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    private void deleteSentMessage(final int position) {
        usersRef.child("Messages")
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    delete(position);
                }
                else
                {
                    delete(position);
                }
            }
        });

//        Query userQuery = ref.child(messageSenderId).orderByChild(messageReceiverId).equalTo(messageID);
//
//        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
//                    appleSnapshot.getRef().removeValue();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void deleteReceiverMessage(final int position, final MessageViewHolder holder) {
        usersRef.child(messageReceiverId).child("Chats").child(messageSenderId).child("Messages").child(messageID)
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                } else {

                }
            }
        });

    }

    private void deleteMessageForEveryOne(final int position, final MessageViewHolder holder) {
        usersRef.child(messageSenderId).child(messageReceiverId).child("Messages").child(messageID)
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    usersRef.child(messageReceiverId).child(messageSenderId).child("Messages").child(userMessagesList.get(position).getMessageID())
                            .child(userMessagesList.get(position).getFrom())
                            .child(userMessagesList.get(position).getTo())
                            .child(userMessagesList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            }
                        }
                    });
                } else {

                }
            }
        });

    }

    public void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource("one");
            player.prepare();
            player.start();
        } catch (IOException e) {
            String TAG = "TAP";
            Log.e(TAG, "prepare() failed");
        }
    }

    public int getDuration(String mUri) {
        int duration;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(mUri);
        String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        duration = Integer.parseInt(time);
        mmr.release();
        return duration;
    }

    public void stopPlaying() {
        player.stop();
        player.release();
        player = null;
    }

    public void onPlay(boolean start) {
        if (start) {
            startPlaying();
            if (fromUserID.equals(messageSenderId)) {
                MessageViewHolder.messageSenderPlay.setImageResource(R.drawable.ic_stop);
            } else {
                MessageViewHolder.messageReceiverPlay.setImageResource(R.drawable.ic_stop);
            }
        } else {
            stopPlaying();
            if (fromUserID.equals(messageSenderId)) {
                MessageViewHolder.messageSenderPlay.setImageResource(R.drawable.ic_play);
            } else {
                MessageViewHolder.messageReceiverPlay.setImageResource(R.drawable.ic_play);
            }
        }
    }

    public void delete(int position){
        userMessagesList.remove(position);
        notifyItemRemoved(position);
    }
}