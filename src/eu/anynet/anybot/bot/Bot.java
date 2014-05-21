/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.bot;

import java.io.IOException;
import java.util.ArrayList;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

/**
 *
 * @author sim
 */
public class Bot extends PircBot
{

   private ArrayList<Module> modules = new ArrayList<>();
   private boolean autoreconnect=false;


   public Bot(String ident, String realname, String ctcpfinger) {
      this.setLogin(ident);
      this.setVersion(realname);
      this.setFinger(ctcpfinger);
   }

   public Bot(String ident)
   {
      this(ident, ident, ident);
   }

   public Bot()
   {
      this("anybot", "anybot-0.0", "anybot-0.0");
   }



   public void addModule(Module newmod)
   {
      if(!this.modules.contains(newmod))
      {
         newmod.setBot(this);
         this.modules.add(newmod);
      }
   }

   private synchronized ArrayList<Module> cloneModuleList()
   {
      return (ArrayList<Module>) this.modules.clone();
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
      ChatMessage newmsg = new ChatMessage(this);
      newmsg.setChannel(channel);
      newmsg.setNick(sender);
      newmsg.setIdent(login);
      newmsg.setHost(hostname);
      newmsg.setMessage(message);

      ArrayList<Module> locallist = this.cloneModuleList();
      for (Module listener : locallist) {
         listener.onMessage(newmsg);
      }
   }


   @Override
   public void onConnect()
   {
      //System.out.println("Connected!");
      ArrayList<Module> locallist = this.cloneModuleList();
      for (Module listener : locallist) {
         listener.onConnect(new ChatEvent(this));
      }
   }

   @Override
   public void onDisconnect()
   {
      if (this.isAutoReconnectEnabled())
      {
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
         } catch (InterruptedException ex) {  }
         return;
      }

      ArrayList<Module> locallist = this.cloneModuleList();
      for (Module listener : locallist) {
         listener.onDisconnect(new ChatEvent(this));
      }
   }

   @Override
   public void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel)
   {
      ChatMessage newmsg = new ChatMessage(this);
      newmsg.setChannel(channel);
      newmsg.setNick(sourceNick);
      newmsg.setIdent(sourceLogin);
      newmsg.setHost(sourceHostname);
      newmsg.setMessage(targetNick);

      ArrayList<Module> locallist = this.cloneModuleList();
      for (Module listener : locallist) {
         listener.onInvite(newmsg);
      }
   }

   @Override
   protected void onJoin(String channel, String sender, String login, String hostname)
   {
      ChatMessage newmsg = new ChatMessage(this);
      newmsg.setChannel(channel);
      newmsg.setNick(sender);
      newmsg.setIdent(login);
      newmsg.setHost(hostname);
      newmsg.setMessage("join");

      ArrayList<Module> locallist = this.cloneModuleList();
      for (Module listener : locallist) {
         listener.onJoin(newmsg);
      }
   }

   @Override
   protected void onPart(String channel, String sender, String login, String hostname)
   {
      ChatMessage newmsg = new ChatMessage(this);
      newmsg.setChannel(channel);
      newmsg.setNick(sender);
      newmsg.setIdent(login);
      newmsg.setHost(hostname);
      newmsg.setMessage("part");

      ArrayList<Module> locallist = this.cloneModuleList();
      for (Module listener : locallist) {
         listener.onPart(newmsg);
      }
   }

   @Override
   protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason)
   {
      ChatMessage newmsg = new ChatMessage(this);
      newmsg.setChannel(channel);
      newmsg.setNick(kickerNick);
      newmsg.setIdent(kickerLogin);
      newmsg.setHost(kickerHostname);
      newmsg.setRecipient(recipientNick);
      newmsg.setMessage(reason);

      ArrayList<Module> locallist = this.cloneModuleList();
      for (Module listener : locallist) {
         listener.onKick(newmsg);
      }
   }



}
