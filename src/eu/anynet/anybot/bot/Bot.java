/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.bot;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

/**
 *
 * @author sim
 */
public class Bot extends PircBot {

    public Bot() {
        this.setName("AnyBot");
    }

   @Override
   public void onMessage(String channel, String sender, String login, String hostname, String message)
   {
      System.out.println("["+channel+"] <"+sender+"> "+message);
      if (message.equalsIgnoreCase("time")) {
         String time = new java.util.Date().toString();
         sendMessage(channel, sender + ": The time is now " + time);
      }
   }


   @Override
   public void onConnect()
   {
      this.joinChannel("#bbot");
   }

   @Override
   public void onDisconnect()
   {
      try {
         this.reconnect();
      } catch (IOException | IrcException ex) {
         Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   @Override
   public void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel)
   {
      this.joinChannel(channel);
   }


}
