/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 *
 * @author sim
 */
public class NamedThreadPool {

   private HashMap<String,Future> threads;
   private ExecutorService pool;

   public NamedThreadPool()
   {
      this.threads = new HashMap<>();
      this.pool = java.util.concurrent.Executors.newCachedThreadPool();
   }

   public boolean instanceExist(String instancename)
   {
      return this.threads.keySet().contains(instancename);
   }

   public void add(Thread thread)
   {
      if(!this.instanceExist(thread.getName()))
      {
         this.threads.put(thread.getName(), this.pool.submit(thread));
      }
   }

   public void cancel(String instancename)
   {
      if(this.instanceExist(instancename))
      {
         this.threads.get(instancename).cancel(true);
      }
   }


}
