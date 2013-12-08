/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot;

import eu.anynet.anybot.bot.BotThread;
import eu.anynet.anybot.bot.ThreadManager;
import eu.anynet.java.util.CommandLineEvent;
import eu.anynet.java.util.CommandLineListener;
import eu.anynet.java.util.CommandLineParser;
import eu.anynet.java.util.PackageScanner;
import eu.anynet.java.util.SaveBoolean;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sim
 */
public class AnyBot {


   public void begin()
   {
      final CommandLineParser parser = new CommandLineParser();
      final ThreadManager pool = new ThreadManager();
      final SaveBoolean isRunning = new SaveBoolean(true);


      parser.addCommandLineListener(new CommandLineListener("^start") {
         @Override
         public void handleCommand(CommandLineEvent e) {
            String host = e.get(1);
            try {
               BotThread newthread = new BotThread();
               newthread.setName(host);
               pool.add(newthread);
               pool.start(host);
            } catch(IOException ex) {
               System.out.println("["+host+"] Could not start: "+ex.getMessage());
            }
         }
      });

      parser.addCommandLineListener(new CommandLineListener("^stop") {
         @Override
         public void handleCommand(CommandLineEvent e) {
            pool.kill(e.get(1));
         }
      });

      parser.addCommandLineListener(new CommandLineListener("^exit") {
         @Override
         public void handleCommand(CommandLineEvent e) {
            pool.killAll();
            isRunning.setFalse();
         }
      });

      parser.addCommandLineListener(new CommandLineListener("^send") {
         @Override
         public void handleCommand(CommandLineEvent e) {
            pool.send(e.get(1), e.get(2, -1)+"\n");
         }
      });

      System.out.println("Welcome to a demo shell!");
      Scanner in = new Scanner(System.in);
      while(isRunning.get())
      {
         System.out.print("> ");
         parser.handleCommandLine(in.nextLine());
      }

      System.out.println("\nBye!");
   }


   /**
    * @param args the command line arguments
    */
   public static void main(String[] args)
   {
      try {
         AnyBot anybot = new AnyBot();
         anybot.begin();


         /*
    private Plugin createPluginInstance(boolean override) throws Exception {
        String pluginname = this.configPluginlist.get(this.pluginList.getSelectedIndex()).get(0);

        if(!this.pluginInstances.containsKey(pluginname) || override) {
            Class myPlugin = Class.forName("comicdownloader.plugins."+pluginname);
            Object o = myPlugin.getConstructor().newInstance();
            myPlugin.getMethod("setGui", new Class[]{GUI.class}).invoke(o, this);
            this.pluginInstances.put(pluginname, (Plugin)o);
            return (Plugin)o;
        } else {
            return this.pluginInstances.get(pluginname);
        }
    }
         */

         List<String> classes = PackageScanner.listClassesInPackage("eu.anynet.anybot.bot");
         for(String cl : classes)
         {
            System.out.println(cl);
         }

      } catch (ClassNotFoundException | IOException ex) {
         Logger.getLogger(AnyBot.class.getName()).log(Level.SEVERE, null, ex);
      }

   }

}
