/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.anynet.anybot.bot;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sim
 */
@XmlRootElement(name = "IRCCommand")
@XmlAccessorType(XmlAccessType.FIELD)
public class IRCCommand
{
   public enum CommandType
   {
      PRIVMSG, RAW
   }

   private CommandType type;
   private String target;
   private String command;

   public CommandType getType() {
      return type;
   }

   public IRCCommand setType(CommandType type) {
      this.type = type;
      return this;
   }

   public String getTarget() {
      return target;
   }

   public IRCCommand setTarget(String target) {
      this.target = target;
      return this;
   }

   public String getCommand() {
      return command;
   }

   public IRCCommand setCommand(String command) {
      this.command = command;
      return this;
   }

   public String buildRawCommand()
   {
      if(this.type==CommandType.RAW)
      {
         return this.getCommand();
      }
      else if(this.type==CommandType.PRIVMSG)
      {
         return "PRIVMSG "+this.getTarget()+" "+this.getCommand();
      }
      else
      {
         return null;
      }
   }


}
