/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.module;

import eu.anynet.anybot.bot.ChatMessage;
import eu.anynet.anybot.bot.Module;
import eu.anynet.java.util.TimerTask;
import java.util.ArrayList;

/**
 *
 * @author sim
 */
public class TimerDemo extends Module {

   private TimerTask tsk;
   private ArrayList<String> enabledchannels;

   public TimerDemo() {
      final TimerDemo me = this;
      this.enabledchannels = new ArrayList<>();

      this.tsk = new TimerTask(5000)
      {
         @Override
         public void doWork()
         {
            ArrayList<String> list;
            synchronized (me)
            {
               list = (ArrayList<String>) me.enabledchannels.clone();
            }
            for(String chan : list)
            {
               me.getBot().sendMessage(chan, "Timer Event!");
            }
         }
      };

   }

   @Override
   public void onMessage(ChatMessage msg)
   {
      if(!msg.get(0).equalsIgnoreCase("timer"))
      {
         return;
      }

      if(msg.get(1).equalsIgnoreCase("enable"))
      {
         if(!this.enabledchannels.contains(msg.getChannel()))
         {
            msg.respond("Enable timer for "+msg.getChannel());
            this.enabledchannels.add(msg.getChannel());
         }
      }
      else if(msg.get(1).equalsIgnoreCase("disable"))
      {
         if(this.enabledchannels.contains(msg.getChannel()))
         {
            msg.respond("Disable timer for "+msg.getChannel());
            this.enabledchannels.remove(msg.getChannel());
         }
      }
      else if(msg.get(1).equalsIgnoreCase("start"))
      {
         msg.respond("Start Timer!");
         try
         {
            this.tsk.start();
         }
         catch(IllegalStateException ex)
         {
            msg.respond(ex.getMessage());
         }
      }
      else if(msg.get(1).equalsIgnoreCase("stop"))
      {
         msg.respond("Stop Timer!");
         this.tsk.stop();
      }
   }



}
