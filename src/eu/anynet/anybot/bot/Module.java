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

   public void onConnect(ChatEvent msg) {  }

   public void onDisconnect(ChatEvent msg) {  }

   public void onJoin(ChatMessage msg) {  }

   public void onPart(ChatMessage msg) {  }

   public void onMessage(ChatMessage msg) {  }

   public void onInvite(ChatMessage msg) {  }

}
