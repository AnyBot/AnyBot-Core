/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.bot;

/**
 *
 * @author sim
 */
public abstract class Module
{

   private boolean isenabled;
   private Bot bot;

   public void onConnect(ChatEvent msg) {  }

   public void onDisconnect(ChatEvent msg) {  }

   public void onJoin(ChatMessage msg) {  }

   public void onPart(ChatMessage msg) {  }

   public void onMessage(ChatMessage msg) {  }

   public void onInvite(ChatMessage msg) {  }

   public boolean isEnabled()
   {
      return this.isenabled;
   }

   public void setBot(Bot b)
   {
      this.bot = b;
   }

   protected Bot getBot()
   {
      if(this.bot==null)
      {
         throw new IllegalArgumentException("Bot object not set");
      }

      return this.bot;
   }

}
