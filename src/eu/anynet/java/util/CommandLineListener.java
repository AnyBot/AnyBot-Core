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

   private String regex;

   public CommandLineListener(String check_regex)
   {
      this.regex = check_regex;
   }

   public boolean isResponsible(String message)
   {
      return Regex.isRegexTrue(message, this.regex);
   }

   abstract public void handleCommand(CommandLineEvent e);

}
