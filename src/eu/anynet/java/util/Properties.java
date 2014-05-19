/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.anynet.java.util;

import java.io.File;
import java.util.HashMap;

/**
 *
 * @author perry
 */
public class Properties
{

   private HashMap<String,String> properties;

   public Properties()
   {
      this.properties = new HashMap<>();
   }

   public void set(String key, String value)
   {
      this.properties.put(key, value);
   }

   public String get(String key)
   {
      return this.properties.get(key);
   }

   public int getInt(String key)
   {
      return Integer.parseInt(this.get(key));
   }

   public double getDouble(String key)
   {
      return Double.parseDouble(this.get(key));
   }

   public long getLong(String key)
   {
      return Long.parseLong(this.get(key));
   }

   public File getFile(String key)
   {
      return new File(this.get(key));
   }

   public boolean getBoolean(String key)
   {
      return (this.get(key).equals("true") || this.get(key).equals("1") || this.get(key).equals("yes"));
   }


}
