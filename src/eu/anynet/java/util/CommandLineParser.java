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
   private ArrayList<String> messagequeue = new ArrayList<>();

   public synchronized void addCommandLineListener(CommandLineListener newl)
   {
      if(!this.commandLineListenerList.contains(newl))
      {
         this.commandLineListenerList.add(newl);
      }
   }
   
   public synchronized void removeCommandLineListener(CommandLineListener l)
   {
      if(this.commandLineListenerList.contains(l))
      {
         this.commandLineListenerList.remove(l);
      }
   }
   
   public String[] consumeMessageQueue()
   {
      String[] queue = this.messagequeue.toArray(new String[this.messagequeue.size()]);
      this.messagequeue.clear();
      return queue;
   }
   
   public void handleCommandLine(String line)
   {
      this.handleCommandLine(new CommandLineEvent(line));
   }

   public boolean handleCommandLine(CommandLineEvent e)
   {
      ArrayList<CommandLineListener> locallist;
      synchronized (this) {
         if (this.commandLineListenerList.isEmpty())
         {
            return false;
         }
         locallist = (ArrayList<CommandLineListener>) this.commandLineListenerList.clone();
      }

      int i=0;
      for (CommandLineListener listener : locallist) {
         if(listener.isResponsible(e.get()))
         {
            i++;
            if(listener.isValid(e.get()))
            {
               listener.handleCommand(e);
            } else if(listener.getUsage()!=null)
            {
               this.messagequeue.add(listener.getUsage());
            }
         }
      }
      
      return (i>0);
   }


}
