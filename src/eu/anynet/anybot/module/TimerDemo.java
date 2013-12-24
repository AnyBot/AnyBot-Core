/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.module;

import eu.anynet.anybot.bot.ChatMessage;
import eu.anynet.anybot.bot.Module;
import eu.anynet.java.util.CommandLineEvent;
import eu.anynet.java.util.CommandLineListener;
import eu.anynet.java.util.CommandLineParser;
import eu.anynet.java.util.TimerTask;
import java.util.ArrayList;

/**
 *
 * @author sim
 */
public class TimerDemo extends Module {

   private TimerTask tsk;
   private ArrayList<String> enabledchannels;

   public TimerDemo()
   {
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
   public void onMessage(final ChatMessage msg)
   {
      CommandLineParser parser = new CommandLineParser();
      final TimerDemo me = this;

      parser.addCommandLineListener(new CommandLineListener(this.buildRegex("timer\\s+enable")) {
         @Override
         public void handleCommand(CommandLineEvent e) {
            if(!me.enabledchannels.contains(msg.getChannel()))
            {
               msg.respond("Enable timer for "+msg.getChannel());
               me.enabledchannels.add(msg.getChannel());
            }
         }
      });

      parser.addCommandLineListener(new CommandLineListener(this.buildRegex("timer\\s+disable")) {
         @Override
         public void handleCommand(CommandLineEvent e) {
            if(me.enabledchannels.contains(msg.getChannel()))
            {
               msg.respond("Disable timer for "+msg.getChannel());
               me.enabledchannels.remove(msg.getChannel());
            }
         }
      });

      parser.addCommandLineListener(new CommandLineListener(this.buildRegex("timer\\s+start")) {
         @Override
         public void handleCommand(CommandLineEvent e) {
            msg.respond("Start Timer!");
            try
            {
               me.tsk.start();
            }
            catch(IllegalStateException ex)
            {
               msg.respond(ex.getMessage());
            }
         }
      });

      parser.addCommandLineListener(new CommandLineListener(this.buildRegex("timer\\s+stop")) {
         @Override
         public void handleCommand(CommandLineEvent e) {
            msg.respond("Stop Timer!");
            me.tsk.stop();
         }
      });

      String alltext = msg.get();
      parser.handleCommandLine(alltext);

   }



}
