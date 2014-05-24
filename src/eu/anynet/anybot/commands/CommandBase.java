/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.anynet.anybot.commands;

import eu.anynet.java.util.CommandLineListener;
import eu.anynet.java.util.CommandLineParser;
import eu.anynet.java.util.SaveBoolean;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reflections.Reflections;

/**
 *
 * @author perry
 */
public abstract class CommandBase implements CommandModule 
{
   
   private CommandLineParser parser;
   private ArrayList<CommandLineListener> loadedCommands;
   private Object[] additionalobjects;
   protected final SaveBoolean isEnabled;

   public CommandBase(CommandLineParser parser, Object[] additionalobjects)
   {
      this.parser = parser;
      this.additionalobjects = additionalobjects;
      this.loadedCommands = new ArrayList<>();
      this.isEnabled = new SaveBoolean(false);
   }
   
   public Object getObjectAt(int i)
   {
      if(this.additionalobjects.length>i)
      {
         return this.additionalobjects[i];
      }
      else
      {
         return null;
      }
   }
   
   public static void loadAll(CommandLineParser parser, Object[] additionalobjects)
   {
      Reflections reflections = new Reflections("eu.anynet.anybot.commands");
      Set<Class<? extends CommandBase>> modules = reflections.getSubTypesOf(CommandBase.class);
      
      for (Class<? extends CommandBase> item : modules) 
      {
         try {
            CommandBase module = item.getConstructor(CommandLineParser.class, Object[].class).newInstance(parser, additionalobjects);
            if(module.isEnabled.isTrue())
            {
               module.load();
            }
         } 
         catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(CommandBase.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }
   
   abstract ArrayList<CommandLineListener> getCommands();
   
   @Override
   public void load() 
   {
      for(CommandLineListener listener : this.getCommands())
      {
         this.parser.addCommandLineListener(listener);
         this.loadedCommands.add(listener);
      }
   }

   @Override
   public void unload() 
   {
      for(CommandLineListener listener : this.getCommands())
      {
         this.parser.removeCommandLineListener(listener);
         this.loadedCommands.remove(listener);
      }
   }
   
}
