/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.bot;

/**
 *
 * @author sim
 */
public class ChatEvent {

   private Bot bot;

   public ChatEvent(Bot bot) {
      this.bot = bot;
   }

   public Bot getBot()
   {
      return this.bot;
   }

   public void sendMessage(String channelornick, String message, boolean action)
   {
      if(action)
      {
         this.getBot().sendAction(channelornick, message);
      }
      else
      {
         this.getBot().sendMessage(channelornick, message);
      }
   }

   public void sendMessage(String channelornick, String message)
   {
      this.sendMessage(channelornick, message, false);
   }

}
