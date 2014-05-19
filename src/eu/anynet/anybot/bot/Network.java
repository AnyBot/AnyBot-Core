/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.anynet.anybot.bot;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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

   @XmlTransient
   private BotThread botthread;

   @XmlTransient
   private Thread output;

   @XmlElementWrapper(name = "AfterConnectCommands")
   @XmlElement(name = "IRCCommand")
   private final ArrayList<IRCCommand> afterConnectCommands;

   @XmlElementWrapper(name = "BeforeDisconnectCommands")
   @XmlElement(name = "IRCCommand")
   private final ArrayList<IRCCommand> beforeDisconnectCommands;


   public Network()
   {
      this.autostart = false;
      this.afterConnectCommands = new ArrayList<>();
      this.beforeDisconnectCommands = new ArrayList<>();
      this.botthread = null;
      this.output = null;
   }

   public BotThread getBotThread() throws IOException
   {
      if(this.botthread==null || this.botthread.isInterrupted())
      {
         this.botthread = new BotThread(this);
         final Network net = this;

         if(this.output!=null && (this.output.isAlive() || !this.output.isInterrupted()))
         {
            this.output.interrupt();
            this.output = null;
         }

         this.output = new Thread() {
            @Override
            public void run()
            {
               while(true)
               {
                  try {
                     String msg = net.botthread.getPipeEndpoint().receive();
                     System.out.println("["+net.getHost()+"] "+msg);
                  } catch (IOException ex) {
                     Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
                  }
               }
            }
         };

         this.output.start();
      }
      return this.botthread;
   }

   public void setAutostart(boolean b)
   {
      this.autostart = b;
   }

   public boolean isAutostartEnabled()
   {
      return this.autostart;
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



}
