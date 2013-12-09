/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.java.util;

/**
 *
 * @author sim
 */
public class CommandLineEvent implements ArgumentInterface {

   private Arguments args;


   public CommandLineEvent(String argstring)
   {
      this.args = new Arguments(argstring);
   }

   @Override
   public int count() {
      return this.args.count();
   }

   @Override
   public String get() {
      return this.args.get();
   }

   @Override
   public String get(int i) {
      return this.args.get(i);
   }

   @Override
   public String get(int start, int end, String glue) {
      return this.args.get(start, end, glue);
   }

   @Override
   public String get(int start, int end) {
      return this.args.get(start, end);
   }



}
