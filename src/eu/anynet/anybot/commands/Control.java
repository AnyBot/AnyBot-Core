/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.anynet.anybot.commands;

import eu.anynet.anybot.AnyBot;
import eu.anynet.anybot.bot.BotThread;
import eu.anynet.anybot.bot.Network;
import eu.anynet.anybot.bot.NetworkSettingsStore;
import eu.anynet.java.util.CommandLineEvent;
import eu.anynet.java.util.CommandLineListener;
import eu.anynet.java.util.CommandLineModuleBase;
import eu.anynet.java.util.CommandLineParser;
import eu.anynet.java.util.SafeBoolean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author perry
 */
public class Control extends CommandLineModuleBase {

   public Control(CommandLineParser parser, Object[] additionalobjects)
   {
      super(parser, additionalobjects);
      this.isEnabled.setTrue();
   }

   @Override
   public ArrayList<CommandLineListener> getCommands()
   {
      final NetworkSettingsStore networks = (NetworkSettingsStore)this.getObjectAt(0);
      final SafeBoolean isRunning = (SafeBoolean)this.getObjectAt(1);
      ArrayList<CommandLineListener> commands = new ArrayList<>();


      commands.add(new CommandLineListener("^start", "^start[\\s]+[a-zA-Z0-9]+") {
         @Override
         public void handleCommand(CommandLineEvent e) {
            String host = e.get(1);
            try {
               if(networks.getNetworkKeys().contains(host))
               {
                  Network network = networks.getNetwork(host);
                  network.setNetworkStore(networks);
                  if(!network.isRunning())
                  {
                     network.start();
                  }
                  else
                  {
                     System.out.println("["+host+"] Network already running.");
                  }
               }
               else
               {
                  System.out.println("["+host+"] Network definition not found.");
               }
            } catch(IOException ex) {
               System.out.println("["+host+"] Could not start: "+ex.getMessage());
            }
         }

         @Override
         public String getUsage() {
            return "start networkname";
         }

      });


      commands.add(new CommandLineListener("^stop", "^stop[\\s]+[a-zA-Z0-9]+") {
         @Override
         public void handleCommand(CommandLineEvent e) {
            String host = e.get(1);
            if(networks.getNetworkKeys().contains(host))
            {
               Network network = networks.getNetwork(host);
               if(network.isRunning())
               {
                  network.stop();
               }
               else
               {
                  System.out.println("["+host+"] Network not running.");
               }
            }
            else
            {
               System.out.println("["+host+"] Network definition not found.");
            }
         }

         @Override
         public String getUsage() {
            return "stop networkname";
         }

      });


      commands.add(new CommandLineListener("^exit") {
         @Override
         public void handleCommand(CommandLineEvent e)
         {
            for(String netname : networks.getNetworkKeys())
            {
               try {
                  BotThread thr = networks.getNetwork(netname).getBotThread();
                  if(thr.isAlive() || !thr.isInterrupted())
                  {
                     thr.interrupt();
                  }
               } catch (IOException ex) {
                  System.out.println("["+netname+"] Could not interrupt: "+ex.getMessage());
               }
            }
            isRunning.setFalse();
         }
      });


      commands.add(new CommandLineListener("^send", "^send[\\s]+[a-zA-Z0-9]+[\\s]+.+") {
         @Override
         public void handleCommand(CommandLineEvent e) {
            String netname = e.get(1);
            try {
               if(networks.getNetworkKeys().contains(netname))
               {
                  String msg = e.get(2, -1);
                  networks.getNetwork(netname).getBotThread().getPipeEndpoint().send(msg+"\n");
               }
               else
               {
                  System.out.println("["+netname+"] Network definition not found");
               }
            } catch (IOException ex) {
               Logger.getLogger(AnyBot.class.getName()).log(Level.SEVERE, null, ex);
            }
         }

         @Override
         public String getUsage() {
            return "send networkname command";
         }

      });


      return commands;
   }

}
