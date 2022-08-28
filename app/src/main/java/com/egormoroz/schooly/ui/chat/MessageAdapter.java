package com.egormoroz.schooly.ui.chat;


import static com.google.android.material.transition.MaterialSharedAxis.X;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.R;
import com.egormoroz.schooly.ui.chat.holders.ImageViewerActivity;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.NewClothesAdapter;
import com.egormoroz.schooly.ui.news.NewsItem;
import com.egormoroz.schooly.ui.profile.LooksAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> userMessagesList;
    private DatabaseReference usersRef;
    private String messageSenderNick = "", messageReceiverNick = "";
    private String fromUserID;
    private DatabaseReference reference;
    private Context context;
    static Clothes trueClothes;
    private MessageFragment messageFragment;
    private GroupChatFragment groupChatFragment;
    static NewsItem newsItem;
    static NewsItem newsItemToViewing;
    private boolean isMpPlaying = false;
    private CurrentVoice currentVoice = new CurrentVoice();
    MessageAdapter.ItemClickListener itemClickListener;


    public MessageAdapter(List<Message> userMessagesList, String messageSenderId, String messageReceiverId, ItemClickListener itemClickListener, MessageFragment messageFragment) {
        this.userMessagesList = userMessagesList;
        this.messageSenderNick = messageSenderId;
        this.messageReceiverNick = messageReceiverId;
        this.itemClickListener = itemClickListener;
        this.messageFragment = messageFragment;
    }

    public MessageAdapter(List<Message> userMessagesList, String messageSenderId, String messageReceiverId, ItemClickListener itemClickListener) {
        this.userMessagesList = userMessagesList;
        this.messageSenderNick = messageSenderId;
        this.messageReceiverNick = messageReceiverId;
        this.itemClickListener = itemClickListener;

    }


//    public MessageAdapter(List<Message> userMessagesList, String messageSenderId) {
//        this.userMessagesList = userMessagesList;
//        this.messageSenderNick = messageSenderId;
//    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receiverMessageText, senderMessageTime,
                receiverMessageTime, clothesTitleAndCreator, senderTimeClothes,
                senderMessageTextClothes, clothesTitleAndCreatorOther, receiverTimeClothesOther,
                receiverMessageTextClothesOther, lookFrom, senderTimeLook, senderMessageTextLook,
                lookFromOther, receiverTimeLook, receiverMessageTextLook;
        //public ImageView receiverProfileImage;
        public RelativeLayout outMessage, inMessage, inClothes, outClothes, inLook, outLook;
        public ImageView messageSenderPicture,  clothesImage, clothesImageOther;
        public ImageView messageReceiverPicture,  watchLookOther, watchLook;

        private void handleShowView(View view) {
            if (getAdapterPosition() > X - 1) {
                view.setVisibility(View.GONE);
                return;
            }
            view.setVisibility(View.VISIBLE);
        }

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            handleShowView(itemView);
            outMessage = itemView.findViewById(R.id.textMessageOutcoming);
            inMessage = itemView.findViewById(R.id.textMessageIncoming);
            receiverMessageTime = itemView.findViewById(R.id.receiver_time);
            senderMessageTime = itemView.findViewById(R.id.sender_time);
            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            //receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            inClothes = itemView.findViewById(R.id.clothesFrom);
            clothesTitleAndCreator = itemView.findViewById(R.id.clothesTitleAndCreator);
            clothesImage = itemView.findViewById(R.id.clothesImage);
            senderTimeClothes = itemView.findViewById(R.id.sender_time_clothes);
            senderMessageTextClothes = itemView.findViewById(R.id.sender_message_text_clothes);
            outClothes = itemView.findViewById(R.id.clothesFromOther);
            clothesTitleAndCreatorOther = itemView.findViewById(R.id.clothesTitleAndCreatorOther);
            clothesImageOther = itemView.findViewById(R.id.clothesImageOther);
            receiverTimeClothesOther = itemView.findViewById(R.id.receiver_time_clothes);
            receiverMessageTextClothesOther = itemView.findViewById(R.id.receiver_message_text_clothes_other);
            inLook = itemView.findViewById(R.id.relativeLook);
            lookFrom = itemView.findViewById(R.id.lookFrom);
            watchLook = itemView.findViewById(R.id.watchLook);
            senderTimeLook = itemView.findViewById(R.id.sender_time_look);
            senderMessageTextLook = itemView.findViewById(R.id.sender_message_text_look);
            outLook = itemView.findViewById(R.id.relativeLookOther);
            lookFromOther = itemView.findViewById(R.id.lookFromOther);
            watchLookOther = itemView.findViewById(R.id.watchLookOther);
            receiverTimeLook = itemView.findViewById(R.id.receiver_time_look_other);
            receiverMessageTextLook = itemView.findViewById(R.id.receiver_message_text_look_other);
        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.messages_types, viewGroup, false);
        reference = FirebaseDatabase.getInstance().getReference("users").child(messageSenderNick).child("Chats").child(messageReceiverNick).child("Messages");
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        return new MessageViewHolder(view);
    }

    class CurrentVoice {
        MediaPlayer mediaPlayer;
        ImageView play, pause;

        public CurrentVoice(MediaPlayer mp, ImageView play, ImageView pause) {
            this.play = play;
            this.pause = pause;
            mediaPlayer = mp;
        }

        public CurrentVoice() {

        }

        public MediaPlayer getMediaPlayer() {
            return mediaPlayer;
        }

        public void setMediaPlayer(MediaPlayer mediaPlayer) {
            this.mediaPlayer = mediaPlayer;
        }

        public ImageView getPlay() {
            return play;
        }

        public void setPlay(ImageView play) {
            this.play = play;
        }

        public ImageView getPause() {
            return pause;
        }

        public void setPause(ImageView pause) {
            this.pause = pause;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, @SuppressLint("RecyclerView") final int position) {
        Message messages = userMessagesList.get(position);
        fromUserID = messages.getFrom();
        MediaPlayer mediaplayer = MediaPlayer.create(messageViewHolder.itemView.getContext(), Uri.parse(messages.getMessage()));
        String fromMessageType = messages.getType();
        int duration;
        final long[] timeBefore = new long[1];


        messageViewHolder.inMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                messageFragment.showChatFunc(messages);
                return true;
            }
        });
        messageViewHolder.outMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                messageFragment.showChatFunc(messages);
                return true;
            }
        });
//        messageViewHolder.inVoice.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                messageFragment.showChatFunc(messages);
//                return true;
//            }
//        });
//        messageViewHolder.outVoice.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                messageFragment.showChatFunc(messages);
//                return true;
//            }
//        });


        //VoicePlayer voicePlayer = new VoicePlayer(messageViewHolder.itemView.getContext());
        double voiceDuration = 1;

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")) {
                    String receiverImage = dataSnapshot.child("image").getValue().toString();

                    //Picasso.get().load(receiverImage).placeholder(R.drawable.ic_image).into(messageViewHolder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        messageViewHolder.outMessage.setVisibility(View.GONE);
        messageViewHolder.inMessage.setVisibility(View.GONE);
        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);
        messageViewHolder.inLook.setVisibility(View.GONE);
        messageViewHolder.outLook.setVisibility(View.GONE);
        messageViewHolder.inClothes.setVisibility(View.GONE);
        messageViewHolder.outClothes.setVisibility(View.GONE);


        switch (fromMessageType) {
            case "text":
                if (fromUserID.equals(messageSenderNick)) {
                    messageViewHolder.outMessage.setVisibility(View.VISIBLE);
                    messageViewHolder.senderMessageText.setText(messages.getMessage());
                    messageViewHolder.senderMessageTime.setText(messages.getTime());
                } else {
                    //messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                    messageViewHolder.inMessage.setVisibility(View.VISIBLE);
                    messageViewHolder.receiverMessageText.setText(messages.getMessage());
                    messageViewHolder.receiverMessageTime.setText(messages.getTime());
                }
                break;
            case "image":
                if (fromUserID.equals(messageSenderNick)) {
                    messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                    Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);
                    messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(messageViewHolder.itemView.getContext(), ImageViewerActivity.class);
                            intent.putExtra("url", userMessagesList.get(position).getMessage());
                            messageViewHolder.itemView.getContext().startActivity(intent);
                            notifyDataSetChanged();
                        }
                    });

                } else {
                    //messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                    messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                    Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);
                    messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(messageViewHolder.itemView.getContext(), ImageViewerActivity.class);
                            intent.putExtra("url", userMessagesList.get(position).getMessage());
                            messageViewHolder.itemView.getContext().startActivity(intent);
                            notifyDataSetChanged();
                        }
                    });
                }
                break;
            case "pdf":

            case "docx":
                if (fromUserID.equals(messageSenderNick)) {
                    messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                    messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                            messageViewHolder.itemView.getContext().startActivity(intent);
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    //messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                    messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                    messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                            messageViewHolder.itemView.getContext().startActivity(intent);
                            notifyDataSetChanged();
                        }
                    });
                }
                break;
            case "clothes":
                if (fromUserID.equals(messageSenderNick)) {
                    messageViewHolder.inClothes.setVisibility(View.VISIBLE);
                    messageViewHolder.clothesTitleAndCreator.setText(messages.getClothes().getClothesTitle() + " " +
                            messageViewHolder.clothesTitleAndCreator.getContext().getResources().getString(R.string.by) + " "
                            + messages.getClothes().getCreator());
                    Picasso.get().load(messages.getClothes().getClothesImage()).into(messageViewHolder.clothesImage);
                    messageViewHolder.senderMessageTextClothes.setText(messages.getMessage());
                    messageViewHolder.senderTimeClothes.setText(messages.getTime());
                    messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClickListener.onItemClick(userMessagesList.get(messageViewHolder.getAdapterPosition()).getClothes(), null);
                            trueClothes = userMessagesList.get(messageViewHolder.getAdapterPosition()).getClothes();
                        }
                    });

                } else {
                    messageViewHolder.outClothes.setVisibility(View.VISIBLE);
                    messageViewHolder.clothesTitleAndCreatorOther.setText(messages.getClothes().getClothesTitle() + " " +
                            messageViewHolder.clothesTitleAndCreator.getContext().getResources().getString(R.string.by) + " "
                            + messages.getClothes().getCreator());

                    Picasso.get().load(messages.getClothes().getClothesImage()).into(messageViewHolder.clothesImageOther);
                    messageViewHolder.receiverMessageTextClothesOther.setText(messages.getMessage());
                    messageViewHolder.receiverTimeClothesOther.setText(messages.getTime());
                    messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                itemClickListener.onItemClick(userMessagesList.get(messageViewHolder.getAdapterPosition()).getClothes(), null);
                                trueClothes = userMessagesList.get(messageViewHolder.getAdapterPosition()).getClothes();
                            } catch (Exception e) {

                            }

                        }
                    });
                }
                break;
            case "look":
                if (fromUserID.equals(messageSenderNick)) {
                    Log.d("#####", "AA   " + messages.getNewsItem());
                    messageViewHolder.inLook.setVisibility(View.VISIBLE);
                    messageViewHolder.lookFrom.setText(
                            messageViewHolder.lookFrom.getContext().getResources().getString(R.string.lookby) + " "
                                    + messages.getNewsItem().getNick());

                    messageViewHolder.senderMessageTextLook.setText(messages.getMessage());
                    messageViewHolder.senderTimeLook.setText(messages.getTime());
                    Picasso.get().load(messages.getNewsItem().getImageUrl()).into(messageViewHolder.watchLook);
                    messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClickListener.onItemClick(null, userMessagesList.get(messageViewHolder.getAdapterPosition()).getNewsItem());
                            newsItemToViewing = userMessagesList.get(messageViewHolder.getAdapterPosition()).getNewsItem();
                        }
                    });
                } else {
                    messageViewHolder.outLook.setVisibility(View.VISIBLE);
                    messageViewHolder.lookFromOther.setText(
                            messageViewHolder.clothesTitleAndCreator.getContext().getResources().getString(R.string.lookby) + " "
                                    + messages.getNewsItem().getNick());


                    messageViewHolder.receiverMessageTextLook.setText(messages.getMessage());
                    messageViewHolder.receiverTimeLook.setText(messages.getTime());
                    Picasso.get().load(messages.getNewsItem().getImageUrl()).into(messageViewHolder.watchLook);

                    messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClickListener.onItemClick(null, userMessagesList.get(messageViewHolder.getAdapterPosition()).getNewsItem());
                            newsItemToViewing = userMessagesList.get(messageViewHolder.getAdapterPosition()).getNewsItem();
                        }
                    });
                }
                break;

        }


    }

    public static void lookInfo(ItemClickListener itemClickListener) {
        itemClickListener.onItemClick(null, newsItemToViewing);
    }


    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


    public void delete(int position) {
        userMessagesList.remove(position);
        notifyItemRemoved(position);
    }

    public interface ItemClickListener {
        void onItemClick(Clothes clothes, NewsItem newsItem);
    }

    public static void singeClothesInfo(NewClothesAdapter.ItemClickListener itemClickListener) {
        itemClickListener.onItemClick(trueClothes);
    }


}