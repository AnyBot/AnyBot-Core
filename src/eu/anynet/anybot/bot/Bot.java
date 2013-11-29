/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

/**
 *
 * @author sim
 */
public class Bot extends PircBot {

   private ArrayList<ConnectListener> connectlistenerList = new ArrayList<>();
   private ArrayList<DisconnectListener> disconnectlistenerList = new ArrayList<>();
   private boolean autoreconnect=false;


   public Bot() {
      this.setName("AnyBot");
   }

   public void enableAutoReconnect(boolean b)
   {
      this.autoreconnect = b;
   }

   public void enableAutoReconnect()
   {
      this.enableAutoReconnect(true);
   }

   public void disableAutoReconnect()
   {
      this.enableAutoReconnect(false);
   }

   public boolean isAutoReconnectEnabled()
   {
      return this.autoreconnect;
   }

   @Override
   public void onMessage(String channel, String sender, String login, String hostname, String message)
   {
      //System.out.println("["+channel+"] <"+sender+"> "+message);
      if (message.equalsIgnoreCase("time")) {
         String time = new java.util.Date().toString();
         sendMessage(channel, sender + ": The time is now " + time+ "(In answer to "+login+")");
      }
   }


   @Override
   public void onConnect()
   {
      System.out.println("Connected!");
      ArrayList<ConnectListener> locallist;
      synchronized (this) {
         if (this.connectlistenerList.isEmpty())
         {
            return;
         }
         locallist = (ArrayList<ConnectListener>) this.connectlistenerList.clone();
      }

      for (ConnectListener listener : locallist) {
         listener.handleConnect(this);
      }
   }

   @Override
   public void onDisconnect()
   {
      if (this.isAutoReconnectEnabled()) {
         System.out.println("Server lost connection, reconnect!");
         try {
            while(true)
            {
               try {
                  this.reconnect();
                  break;
               }
               catch(IrcException|IOException ex)
               {
                  Thread.sleep(60000);
               }
            }
         } catch (InterruptedException ex) {
            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
         }
      }

      ArrayList<DisconnectListener> locallist;
      synchronized (this) {
         if (this.disconnectlistenerList.isEmpty())
         {
            return;
         }
         locallist = (ArrayList<DisconnectListener>) this.disconnectlistenerList.clone();
      }

      for (DisconnectListener listener : locallist) {
         listener.handleDisconnect(this);
      }
   }

   @Override
   public void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel)
   {
      this.joinChannel(channel);
   }


   public synchronized void addConnectListener(ConnectListener newl)
   {
      if(!this.connectlistenerList.contains(newl))
      {
         this.connectlistenerList.add(newl);
      }
   }


   public synchronized void addDisconnectListener(DisconnectListener newl)
   {
      if(!this.disconnectlistenerList.contains(newl))
      {
         this.disconnectlistenerList.add(newl);
      }
   }


}
