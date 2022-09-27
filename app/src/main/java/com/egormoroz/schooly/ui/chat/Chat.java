package com.egormoroz.schooly.ui.chat;

import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.people.UserPeopleAdapter;

import java.util.ArrayList;

public class Chat {
  private String name = "";
  private String LastTime = "";
  private String LastMessage = "";
  private String type = "";
  private long unreadMessages;
  ArrayList<UserPeopleAdapter> members;
  private String notificationType;
  ArrayList<String> dialogueMaterials;
  private long timeMill;
  long voiceDuration;
  String chatId;
  String owner;

  public Chat (String name, String LastTime, String LastMessage, String type,long unreadMessages,
               ArrayList<UserPeopleAdapter> members,String notificationType,ArrayList<String> dialogueMaterials
          ,long timeMill,long voiceDuration,String chatId,String owner){
    this.name = name;
    this.LastTime = LastTime;
    this.LastMessage = LastMessage;
    this.type = type;
    this.unreadMessages=unreadMessages;
    this.members=members;
    this.notificationType=notificationType;
    this.dialogueMaterials=dialogueMaterials;
    this.timeMill=timeMill;
    this.voiceDuration=voiceDuration;
    this.chatId=chatId;
    this.owner=owner;
  }

  public Chat() {

  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName () {return  this.name;}

  public void setName (String name) {this.name = name;}

  public String getLastMessage () {return  this.LastMessage;}

  public void setLastMessage (String Messages) {this.LastMessage = Messages;}

  public String getLastTime () {return  this.LastTime;}

  public void setLastTime (String time) {this.LastTime = time;}

  public long getUnreadMessages () {return  this.unreadMessages;}

  public void setUnreadMessages (long unreadMessages) {this.unreadMessages = unreadMessages;}

  public ArrayList<UserPeopleAdapter> getMembers () {return  this.members;}

  public void setMembers (ArrayList<UserPeopleAdapter> members) {this.members = members;}

  public String getNotificationType () {return  this.notificationType;}

  public void setNotificationType (String notificationType) {this.notificationType = notificationType;}

  public ArrayList<String> getDialogueMaterials () {return  this.dialogueMaterials;}

  public void setDialogueMaterials (ArrayList<String> dialogueMaterials) {this.dialogueMaterials = dialogueMaterials;}

  public long getTimeMill () {return  this.timeMill;}

  public void setTimeMill (long timeMill) {this.timeMill = timeMill;}

  public long getVoiceDuration () {return  this.voiceDuration;}

  public void setVoiceDuration (long voiceDuration) {this.voiceDuration = voiceDuration;}

  public String getChatId () {return  this.chatId;}

  public void setChatId (String chatId) {this.chatId = chatId;}

  public String getOwner () {return  this.owner;}

  public void setOwner (String owner) {this.owner = owner;}
}