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
      CHANNEL, USER, RAW
   }

   private CommandType type;
   private String target;
   private String command;

   public CommandType getType() {
      return type;
   }

   public void setType(CommandType type) {
      this.type = type;
   }

   public String getTarget() {
      return target;
   }

   public void setTarget(String target) {
      this.target = target;
   }

   public String getCommand() {
      return command;
   }

   public void setCommand(String command) {
      this.command = command;
   }


}
