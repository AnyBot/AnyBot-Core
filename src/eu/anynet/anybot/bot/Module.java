/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.bot;

import eu.anynet.java.util.Regex;

/**
 *
 * @author sim
 */
public abstract class Module
{

   private boolean isenabled;
   private Bot bot;

   public void onConnect(final ChatEvent msg) {  }

   public void onDisconnect(final ChatEvent msg) {  }

   public void onJoin(final ChatMessage msg) {  }

   public void onPart(final ChatMessage msg) {  }

   public void onKick(final ChatMessage msg) {  }

   public void onMessage(final ChatMessage msg) {  }

   public void onInvite(final ChatMessage msg) {  }

   public boolean isEnabled()
   {
      return this.isenabled;
   }

   public void setBot(final Bot b)
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

   public String buildRegex(String regex)
   {
      String qnick = Regex.quote(this.getBot().getNick());
      String newrgx = "^"+qnick+"[:,]\\s+"+regex;
      return newrgx;
   }

}
