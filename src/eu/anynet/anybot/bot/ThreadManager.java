/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sim
 */
public class ThreadManager {

   private HashMap<String,BotThread> threads;

   public ThreadManager()
   {
      this.threads = new HashMap<>();
   }

   public boolean instanceExist(String instancename)
   {
      return this.threads.keySet().contains(instancename);
   }

   public void add(BotThread thread)
   {
      if(!this.instanceExist(thread.getName()))
      {
         this.threads.put(thread.getName(), thread);
      }
   }

   public void send(String instancename, String message)
   {
      try {
         this.threads.get(instancename).getPipeEndpoint().send(message);
      } catch (IOException ex) {
         Logger.getLogger(ThreadManager.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   public void kill(String instancename)
   {
      if(this.instanceExist(instancename))
      {
         this.threads.get(instancename).interrupt();
      }
   }

   public void start(String instancename)
   {
      if(this.instanceExist(instancename))
      {
         this.threads.get(instancename).start();
      }
   }


}
