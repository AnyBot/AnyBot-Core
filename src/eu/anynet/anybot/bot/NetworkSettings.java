/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.anynet.anybot.bot;

import eu.anynet.java.util.Serializable;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sim
 */
@XmlRootElement(namespace = "eu.anynet.anybot.bot")
@XmlAccessorType(XmlAccessType.FIELD)
public class NetworkSettings extends Serializable<NetworkSettings>
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
      this.initSerializer(this, NetworkSettings.class);
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

   @Override
   public String getSerializerPraefix() {
      return "networksettings-"+this.getHost();
   }

}
