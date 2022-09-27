package com.egormoroz.schooly.ui.chat;


import static com.google.android.material.transition.MaterialSharedAxis.X;

import android.annotation.SuppressLint;
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

import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.chat.holders.ImageViewerActivity;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.NewClothesAdapter;
import com.egormoroz.schooly.ui.news.NewsItem;
import com.egormoroz.schooly.ui.people.PeopleFragment;
import com.egormoroz.schooly.ui.people.UserPeopleAdapter;
import com.egormoroz.schooly.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.GroupChatViewHolder> {
    private List<Message> userMessagesList;
    private DatabaseReference usersRef;
    private String messageSenderNick = "", messageReceiverNick = "";
    private String fromUserID;
    private DatabaseReference reference;
    static Clothes trueClothes;
    private MessageFragment messageFragment;
    private GroupChatFragment groupChatFragment;
    static  NewsItem newsItemToViewing;
    private boolean isMpPlaying = false;
    private CurrentVoice currentVoice = new CurrentVoice();
    GroupChatAdapter.ItemClickListener itemClickListener;
    FirebaseModel firebaseModel=new FirebaseModel();



    public GroupChatAdapter(List<Message> userMessagesList, String messageSenderId, String messageReceiverId, ItemClickListener itemClickListener, GroupChatFragment groupChatFragment) {
        this.userMessagesList = userMessagesList;
        this.messageSenderNick = messageSenderId;
        this.messageReceiverNick = messageReceiverId;
        this.itemClickListener = itemClickListener;
        this.groupChatFragment = groupChatFragment;
    }

    public GroupChatAdapter(List<Message> userMessagesList, String messageSenderId, String messageReceiverId, ItemClickListener itemClickListener) {
        this.userMessagesList = userMessagesList;
        this.messageSenderNick = messageSenderId;
        this.messageReceiverNick = messageReceiverId;
        this.itemClickListener = itemClickListener;

    }



    public static class GroupChatViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receiverMessageText, senderMessageTime,
                receiverMessageTime,   clothesTitleAndCreator, senderTimeClothes,
                senderMessageTextClothes, clothesTitleAndCreatorOther, receiverTimeClothesOther,
                receiverMessageTextClothesOther, lookFrom, senderTimeLook, senderMessageTextLook,
                lookFromOther, receiverTimeLook, receiverMessageTextLook;
        public RelativeLayout outMessage, inMessage, inClothes, outClothes, inLook, outLook;
        public ImageView messageSenderPicture, clothesImage, clothesImageOther;
        public ImageView messageReceiverPicture,  watchLookOther, watchLook;
        ImageView userAvatarIncomingText,userAvatarIncomingClothes,userAvatarIncomingLook
                ,userAvatarIncomingImage;

        private void handleShowView(View view) {
            if (getAdapterPosition() > X - 1) {
                view.setVisibility(View.GONE);
                return;
            }
            view.setVisibility(View.VISIBLE);
        }

        public GroupChatViewHolder(@NonNull View itemView) {
            super(itemView);
            handleShowView(itemView);
            outMessage = itemView.findViewById(R.id.textMessageOutcoming);
            inMessage = itemView.findViewById(R.id.allIncomingText);
            receiverMessageTime = itemView.findViewById(R.id.receiver_time);
            senderMessageTime = itemView.findViewById(R.id.sender_time);
            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            inClothes = itemView.findViewById(R.id.clothesFrom);
            clothesTitleAndCreator = itemView.findViewById(R.id.clothesTitleAndCreator);
            clothesImage = itemView.findViewById(R.id.clothesImage);
            senderTimeClothes = itemView.findViewById(R.id.sender_time_clothes);
            senderMessageTextClothes = itemView.findViewById(R.id.sender_message_text_clothes);
            outClothes = itemView.findViewById(R.id.allClothesIncoming);
            clothesTitleAndCreatorOther = itemView.findViewById(R.id.clothesTitleAndCreatorOther);
            clothesImageOther = itemView.findViewById(R.id.clothesImageOther);
            receiverTimeClothesOther = itemView.findViewById(R.id.receiver_time_clothes);
            receiverMessageTextClothesOther = itemView.findViewById(R.id.receiver_message_text_clothes_other);
            inLook = itemView.findViewById(R.id.relativeLook);
            lookFrom = itemView.findViewById(R.id.lookFrom);
            watchLook = itemView.findViewById(R.id.watchLook);
            senderTimeLook = itemView.findViewById(R.id.sender_time_look);
            senderMessageTextLook = itemView.findViewById(R.id.sender_message_text_look);
            outLook = itemView.findViewById(R.id.allLookIncoming);
            lookFromOther = itemView.findViewById(R.id.lookFromOther);
            watchLookOther = itemView.findViewById(R.id.watchLookOther);
            receiverTimeLook = itemView.findViewById(R.id.receiver_time_look_other);
            receiverMessageTextLook = itemView.findViewById(R.id.receiver_message_text_look_other);
            userAvatarIncomingText=itemView.findViewById(R.id.userAvatarIncomingText);
            userAvatarIncomingImage=itemView.findViewById(R.id.userAvatarIncomingImage);
            userAvatarIncomingClothes=itemView.findViewById(R.id.userAvatarIncomingClothes);
            userAvatarIncomingLook=itemView.findViewById(R.id.userAvatarIncomingLook);
        }
    }


    @NonNull
    @Override
    public GroupChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.messages_types_groups, viewGroup, false);
        reference = FirebaseDatabase.getInstance().getReference("users").child(messageSenderNick).child("Chats").child(messageReceiverNick).child("Messages");
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        firebaseModel.initAll();
        return new GroupChatViewHolder(view);
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
    public void onBindViewHolder(@NonNull final GroupChatViewHolder groupChatViewHolder, @SuppressLint("RecyclerView") final int position) {
        Message messages = userMessagesList.get(position);
        fromUserID = messages.getFrom();
        MediaPlayer mediaplayer = MediaPlayer.create(groupChatViewHolder.itemView.getContext(), Uri.parse(messages.getMessage()));
        String fromMessageType = messages.getType();
        int duration;
        final long[] timeBefore = new long[1];

        groupChatViewHolder.inMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Message message=null;
                if(groupChatViewHolder.getAdapterPosition()==userMessagesList.size()-1&&userMessagesList.size()>1){
                    message=userMessagesList.get(groupChatViewHolder.getAdapterPosition()-1);
                }
                groupChatFragment.showChatFunc(userMessagesList.get(groupChatViewHolder.getAdapterPosition()), groupChatViewHolder.getAdapterPosition(),message);
                return true;
            }
        });
        groupChatViewHolder.outMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Message message=null;
                if(groupChatViewHolder.getAdapterPosition()==userMessagesList.size()-1&&userMessagesList.size()>1){
                    message=userMessagesList.get(groupChatViewHolder.getAdapterPosition()-1);
                }
                groupChatFragment.showChatFunc(userMessagesList.get(groupChatViewHolder.getAdapterPosition()), groupChatViewHolder.getAdapterPosition(),message);
                return true;
            }
        });
//        groupChatViewHolder.inVoice.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                groupChatFragment.showChatFunc(messages);
//                return true;
//            }
//        });
//        groupChatViewHolder.outVoice.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                groupChatFragment.showChatFunc(messages);
//                return true;
//            }
//        });


        //VoicePlayer voicePlayer = new VoicePlayer(groupChatViewHolder.itemView.getContext());
        double voiceDuration = 1;

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")) {
                    String receiverImage = dataSnapshot.child("image").getValue().toString();

                    //Picasso.get().load(receiverImage).placeholder(R.drawable.ic_image).into(groupChatViewHolder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        groupChatViewHolder.outMessage.setVisibility(View.GONE);
        groupChatViewHolder.inMessage.setVisibility(View.GONE);
        groupChatViewHolder.messageSenderPicture.setVisibility(View.GONE);
        groupChatViewHolder.messageReceiverPicture.setVisibility(View.GONE);
        groupChatViewHolder.inLook.setVisibility(View.GONE);
        groupChatViewHolder.outLook.setVisibility(View.GONE);
        groupChatViewHolder.inClothes.setVisibility(View.GONE);
        groupChatViewHolder.outClothes.setVisibility(View.GONE);


        switch (fromMessageType) {
            case "text":
                if (fromUserID.equals(messageSenderNick)) {
                    groupChatViewHolder.outMessage.setVisibility(View.VISIBLE);
                    groupChatViewHolder.senderMessageText.setText(messages.getMessage());
                    groupChatViewHolder.senderMessageTime.setText(messages.getTime());
                } else {
                    groupChatViewHolder.inMessage.setVisibility(View.VISIBLE);
                    groupChatViewHolder.receiverMessageText.setText(messages.getMessage());
                    groupChatViewHolder.receiverMessageTime.setText(messages.getTime());
                    firebaseModel.getUsersReference().child(messages.getFrom()).child("personImage")
                            .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DataSnapshot snapshot=task.getResult();
                                        Picasso.get().load(snapshot.getValue(String.class)).into(groupChatViewHolder.userAvatarIncomingText);
                                    }
                                }
                            });
                    groupChatViewHolder.userAvatarIncomingText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", messages.getFrom(), groupChatFragment,groupChatFragment.userInformation,groupChatFragment.bundle),
                                    groupChatFragment.getActivity());
                        }
                    });
                }
                break;
            case "image":
                if (fromUserID.equals(messageSenderNick)) {
                    groupChatViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                    Picasso.get().load(messages.getMessage()).into(groupChatViewHolder.messageSenderPicture);
                    groupChatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(groupChatViewHolder.itemView.getContext(), ImageViewerActivity.class);
                            intent.putExtra("url", userMessagesList.get(position).getMessage());
                            groupChatViewHolder.itemView.getContext().startActivity(intent);
                            notifyDataSetChanged();
                        }
                    });

                } else {
                    //groupChatViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                    groupChatViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                    Picasso.get().load(messages.getMessage()).into(groupChatViewHolder.messageReceiverPicture);
                    groupChatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(groupChatViewHolder.itemView.getContext(), ImageViewerActivity.class);
                            intent.putExtra("url", userMessagesList.get(position).getMessage());
                            groupChatViewHolder.itemView.getContext().startActivity(intent);
                            notifyDataSetChanged();
                        }
                    });
                    firebaseModel.getUsersReference().child(messages.getFrom()).child("personImage")
                            .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if(task.isSuccessful()){
                                        Log.d("AAAAA", messages.getFrom());
                                        DataSnapshot snapshot=task.getResult();
                                        Picasso.get().load(snapshot.getValue(String.class)).into(groupChatViewHolder.userAvatarIncomingImage);
                                    }
                                }
                            });
                    groupChatViewHolder.userAvatarIncomingImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", messages.getFrom(), groupChatFragment,groupChatFragment.userInformation,groupChatFragment.bundle),
                                    groupChatFragment.getActivity());
                        }
                    });
                }
                break;
            case "pdf":

            case "docx":
                if (fromUserID.equals(messageSenderNick)) {
                    groupChatViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                    groupChatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                            groupChatViewHolder.itemView.getContext().startActivity(intent);
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    //groupChatViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                    groupChatViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                    groupChatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                            groupChatViewHolder.itemView.getContext().startActivity(intent);
                            notifyDataSetChanged();
                        }
                    });
                    firebaseModel.getUsersReference().child(messages.getFrom()).child("personImage")
                            .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DataSnapshot snapshot=task.getResult();
                                        Picasso.get().load(snapshot.getValue(String.class)).into(groupChatViewHolder.userAvatarIncomingImage);
                                    }
                                }
                            });
                    groupChatViewHolder.userAvatarIncomingImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", messages.getFrom(), groupChatFragment,groupChatFragment.userInformation,groupChatFragment.bundle),
                                    groupChatFragment.getActivity());
                        }
                    });
                }
                break;
            case "clothes":
                if (fromUserID.equals(messageSenderNick)) {
                    groupChatViewHolder.inClothes.setVisibility(View.VISIBLE);
                    groupChatViewHolder.clothesTitleAndCreator.setText(messages.getClothes().getClothesTitle() + " " +
                            groupChatViewHolder.clothesTitleAndCreator.getContext().getResources().getString(R.string.by) + " "
                            + messages.getClothes().getCreator());
                    Picasso.get().load(messages.getClothes().getClothesImage()).into(groupChatViewHolder.clothesImage);
                    groupChatViewHolder.senderMessageTextClothes.setText(messages.getMessage());
                    groupChatViewHolder.senderTimeClothes.setText(messages.getTime());
                    groupChatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClickListener.onItemClick(userMessagesList.get(groupChatViewHolder.getAdapterPosition()).getClothes(), null);
                            trueClothes = userMessagesList.get(groupChatViewHolder.getAdapterPosition()).getClothes();
                        }
                    });

                } else {
                    groupChatViewHolder.outClothes.setVisibility(View.VISIBLE);
                    groupChatViewHolder.clothesTitleAndCreatorOther.setText(messages.getClothes().getClothesTitle() + " " +
                            groupChatViewHolder.clothesTitleAndCreator.getContext().getResources().getString(R.string.by) + " "
                            + messages.getClothes().getCreator());

                    Picasso.get().load(messages.getClothes().getClothesImage()).into(groupChatViewHolder.clothesImageOther);
                    groupChatViewHolder.receiverMessageTextClothesOther.setText(messages.getMessage());
                    groupChatViewHolder.receiverTimeClothesOther.setText(messages.getTime());
                    groupChatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClickListener.onItemClick(userMessagesList.get(groupChatViewHolder.getAdapterPosition()).getClothes(), null);
                            trueClothes = userMessagesList.get(groupChatViewHolder.getAdapterPosition()).getClothes();
                        }
                    });
                    firebaseModel.getUsersReference().child(messages.getFrom()).child("personImage")
                            .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DataSnapshot snapshot=task.getResult();
                                        Picasso.get().load(snapshot.getValue(String.class)).into(groupChatViewHolder.userAvatarIncomingClothes);
                                    }
                                }
                            });
                    groupChatViewHolder.userAvatarIncomingClothes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", messages.getFrom(), groupChatFragment,groupChatFragment.userInformation,groupChatFragment.bundle),
                                    groupChatFragment.getActivity());
                        }
                    });
                }
                break;
            case "look":
                if (fromUserID.equals(messageSenderNick)) {
                    Log.d("#####", "AA   " + messages.getNewsItem());
                    groupChatViewHolder.inLook.setVisibility(View.VISIBLE);
                    groupChatViewHolder.lookFrom.setText(
                            groupChatViewHolder.lookFrom.getContext().getResources().getString(R.string.lookby) + " "
                                    + messages.getNewsItem().getNick());

                    groupChatViewHolder.senderMessageTextLook.setText(messages.getMessage());
                    groupChatViewHolder.senderTimeLook.setText(messages.getTime());
                    Picasso.get().load(messages.getNewsItem().getImageUrl()).into(groupChatViewHolder.watchLook);
                    groupChatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClickListener.onItemClick(null, userMessagesList.get(groupChatViewHolder.getAdapterPosition()).getNewsItem());
                            newsItemToViewing = userMessagesList.get(groupChatViewHolder.getAdapterPosition()).getNewsItem();
                        }
                    });
                } else {
                    groupChatViewHolder.outLook.setVisibility(View.VISIBLE);
                    groupChatViewHolder.lookFromOther.setText(
                            groupChatViewHolder.clothesTitleAndCreator.getContext().getResources().getString(R.string.lookby) + " "
                                    + messages.getNewsItem().getNick());


                    groupChatViewHolder.receiverMessageTextLook.setText(messages.getMessage());
                    groupChatViewHolder.receiverTimeLook.setText(messages.getTime());
                    Picasso.get().load(messages.getNewsItem().getImageUrl()).into(groupChatViewHolder.watchLookOther);

                    groupChatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClickListener.onItemClick(null, userMessagesList.get(groupChatViewHolder.getAdapterPosition()).getNewsItem());
                            newsItemToViewing = userMessagesList.get(groupChatViewHolder.getAdapterPosition()).getNewsItem();
                        }
                    });
                    firebaseModel.getUsersReference().child(messages.getFrom()).child("personImage")
                            .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DataSnapshot snapshot=task.getResult();
                                        Picasso.get().load(snapshot.getValue(String.class)).into(groupChatViewHolder.userAvatarIncomingLook);
                                    }
                                }
                            });
                    groupChatViewHolder.userAvatarIncomingLook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", messages.getFrom(), groupChatFragment,groupChatFragment.userInformation,groupChatFragment.bundle),
                                    groupChatFragment.getActivity());
                        }
                    });
                }
                break;

        }


    }


    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


    public void delete(int position) {
        userMessagesList.remove(position);
        notifyItemRemoved(position);
    }

    public static void lookInfo(ItemClickListener itemClickListener){
        itemClickListener.onItemClick(null, newsItemToViewing);
    }

    public interface ItemClickListener {
        void onItemClick(Clothes clothes, NewsItem newsItem);
    }

    public static void singeClothesInfo(NewClothesAdapter.ItemClickListener itemClickListener) {
        itemClickListener.onItemClick(trueClothes);
    }


}