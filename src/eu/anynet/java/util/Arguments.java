/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.java.util;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author sim
 */
public class Arguments {

   private String argstring;
   private ArrayList<String> args;


   public Arguments(String argstring)
   {
      this.argstring = argstring.trim();
      while(this.argstring.indexOf("  ")>-1)
      {
         this.argstring = this.argstring.replace("  ", " ");
      }

      this.args = new ArrayList<>();

      String[] argparts = this.argstring.split(" ");

      for(String argpart : argparts)
      {
         if(!argpart.trim().isEmpty())
         {
            this.args.add(argpart.trim());
         }
      }
   }

   public int count()
   {
      return this.args.size();
   }

   public String get()
   {
      return this.argstring;
   }

   public String get(int i)
   {
      return this.args.get(i);
   }

   public String get(int start, int end, String glue)
   {
      if(end<start)
      {
         end = this.count()-1;
      }

      String[] parts = new String[(end-start+1)];
      for(int i=start, j=0; i<=end; i++, j++)
      {
         parts[j] = this.get(i);
      }
      return StringUtils.join(parts, glue);
   }

   public String get(int start, int end)
   {
      return this.get(start, end, " ");
   }

}
