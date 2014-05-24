/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.java.util;

import java.util.EventListener;

/**
 *
 * @author sim
 */
abstract public class CommandLineListener implements EventListener {

   private final String regex;
   private final String valid_regex;

   public CommandLineListener(String regex, String valid_regex)
   {
      this.regex = regex;
      this.valid_regex = valid_regex;
   }
   
   public CommandLineListener(String regex)
   {
      this(regex, regex);
   }

   public boolean isResponsible(String message)
   {
      return Regex.isRegexTrue(message, this.regex);
   }
   
   public boolean isValid(String message)
   {
      return Regex.isRegexTrue(message, this.valid_regex);
   }

   abstract public void handleCommand(CommandLineEvent e);
   
   public String getUsage()
   {
      return null;
   }

}
