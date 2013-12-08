/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.java.util;

import java.util.ArrayList;

/**
 *
 * @author sim
 */
public class CommandLineParser {

   private ArrayList<CommandLineListener> commandLineListenerList = new ArrayList<>();

   public synchronized void addCommandLineListener(CommandLineListener newl)
   {
      if(!this.commandLineListenerList.contains(newl))
      {
         this.commandLineListenerList.add(newl);
      }
   }

   public void handleCommandLine(String line)
   {
      this.handleCommandLine(new CommandLineEvent(line));
   }

   public void handleCommandLine(CommandLineEvent e)
   {
      ArrayList<CommandLineListener> locallist;
      synchronized (this) {
         if (this.commandLineListenerList.isEmpty())
         {
            return;
         }
         locallist = (ArrayList<CommandLineListener>) this.commandLineListenerList.clone();
      }

      for (CommandLineListener listener : locallist) {
         if(listener.isResponsible(e.get()))
         {
            listener.handleCommand(e);
         }
      }
   }


}
