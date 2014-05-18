/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.anynet.anybot.bot;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sim
 */
@XmlRootElement(name = "NetworkSettings")
public class NetworkSettings
{
   
   private boolean autostart;
   private String host;
   private int port;
   private boolean ssl;
   private String botNickname;
   private String botIdent;
   private String botRealname;

   @XmlElementWrapper(name = "AfterConnectCommands")
   @XmlElement(name = "IRCCommand")
   private final ArrayList<IRCCommand> afterConnectCommands;

   @XmlElementWrapper(name = "BeforeDisconnectCommands")
   @XmlElement(name = "IRCCommand")
   private final ArrayList<IRCCommand> beforeDisconnectCommands;


   public NetworkSettings()
   {
      this.autostart = false;
      this.afterConnectCommands = new ArrayList<>();
      this.beforeDisconnectCommands = new ArrayList<>();
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
