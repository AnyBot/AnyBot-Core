/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.anynet.anybot;

import eu.anynet.anybot.bot.IRCCommand;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sim
 */
@XmlRootElement(name = "NetworkSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class Network
{

   private boolean autostart;
   private String host;
   private int port;
   private boolean ssl;
   private String botNickname;
   private String botIdent;
   private String botRealname;
   private String debugChannel;

   @XmlTransient
   private BotThread botthread;

   @XmlTransient
   private Thread output;

   @XmlTransient
   private NetworkSettingsStore networkstore;

   @XmlElementWrapper(name = "AfterConnectCommands")
   @XmlElement(name = "IRCCommand")
   private final ArrayList<IRCCommand> afterConnectCommands;

   @XmlElementWrapper(name = "BeforeDisconnectCommands")
   @XmlElement(name = "IRCCommand")
   private final ArrayList<IRCCommand> beforeDisconnectCommands;

   @XmlElementWrapper(name = "JoinedChannels")
   @XmlElement(name = "JoinedChannel")
   private final ArrayList<String> joinedChannels;


   public Network()
   {
      this.autostart = false;
      this.afterConnectCommands = new ArrayList<>();
      this.beforeDisconnectCommands = new ArrayList<>();
      this.joinedChannels = new ArrayList<>();
      this.botthread = null;
      this.output = null;
   }

   public void setNetworkStore(NetworkSettingsStore store)
   {
      this.networkstore = store;
   }

   public BotThread getBotThread() throws IOException
   {
      if(this.botthread==null || this.botthread.isInterrupted())
      {
         this.botthread = new BotThread(this);
         final Network net = this;

         for(IRCCommand cmd : this.afterConnectCommands)
         {
            this.botthread.addStartupCommand(cmd);
         }

         if(this.output!=null && (this.output.isAlive() || !this.output.isInterrupted()))
         {
            this.output.interrupt();
            this.output = null;
         }

         this.output = new Thread() {
            @Override
            public void run()
            {
               try {
                  while(true)
                  {
                     String msg = net.botthread.getPipeEndpoint().receive();
                     System.out.println("["+net.getHost()+"] "+msg);
                  }
               } catch (Exception ex) {  }
            }
         };

         this.output.start();
      }
      return this.botthread;
   }

   public boolean isRunning()
   {
      return (this.botthread!=null && this.botthread.isAlive() && !this.botthread.isInterrupted());
   }

   public void stop()
   {
      if(this.botthread.isAlive() || !this.botthread.isInterrupted())
      {
         this.botthread.interrupt();
         this.botthread = null;
      }
      if(this.output.isAlive() || this.output.isInterrupted())
      {
         this.output.interrupt();
         this.output = null;
      }
   }

   public void start() throws IOException
   {
      if(this.botthread==null && this.output==null)
      {
         this.getBotThread().start();
      }
   }

   public void setAutostart(boolean b)
   {
      this.autostart = b;
   }

   public boolean isAutostartEnabled()
   {
      return this.autostart;
   }

   public void setDebugChannel(String channel)
   {
      if(channel==null || channel.trim().length()<1)
      {
         this.debugChannel=null;
      }
      else if(channel.startsWith("#") && channel.length()>1)
      {
         this.debugChannel = channel;
      }
   }

   public String getDebugChannel()
   {
      return this.debugChannel;
   }

   public boolean isDebugChannelSet()
   {
      return (this.debugChannel!=null && this.debugChannel.startsWith("#") && this.debugChannel.length()>1);
   }

   public String getHost() {
      return host;
   }

   public void setHost(String host) {
      this.host = host;
   }

   public int getPort() {
      return port;
   }

   public void setPort(int port) {
      this.port = port;
   }

   public boolean isSsl() {
      return ssl;
   }

   public void setSsl(boolean ssl) {
      this.ssl = ssl;
   }

   public String getBotNickname() {
      return botNickname;
   }

   public void setBotNickname(String botNickname) {
      this.botNickname = botNickname;
   }

   public String getBotIdent() {
      return botIdent;
   }

   public void setBotIdent(String botIdent) {
      this.botIdent = botIdent;
   }

   public String getBotRealname() {
      return botRealname;
   }

   public void setBotRealname(String botRealname) {
      this.botRealname = botRealname;
   }

   public void addAfterConnectCommand(IRCCommand cmd)
   {
      this.afterConnectCommands.add(cmd);
   }

   public void addBeforeDisconnectCommand(IRCCommand cmd)
   {
      this.beforeDisconnectCommands.add(cmd);
   }

   public void addJoinedChannel(String channel)
   {
      if(!this.joinedChannels.contains(channel))
      {
         this.joinedChannels.add(channel);
      }
   }

   public void removeJoinedChannel(String channel)
   {
      if(this.joinedChannels.contains(channel))
      {
         this.joinedChannels.remove(channel);
      }
   }

   public String[] getJoinedChannels()
   {
      return this.joinedChannels.toArray(new String[] {  });
   }

   public void serialize()
   {
      if(this.networkstore!=null)
      {
         this.networkstore.serialize();
      }
   }


}
