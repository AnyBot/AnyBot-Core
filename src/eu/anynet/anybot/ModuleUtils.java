/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.anynet.anybot;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author sim
 */
public class ModuleUtils
{

   public static File[] getModuleFiles()
   {
      String modulefolder = AnyBot.properties.get("fs.execdir")+"modules"+File.separator;
      File[] jars = new File(modulefolder).listFiles(new FileFilter()
      {
         @Override
         public boolean accept(File pathname) {
            return pathname.getName().endsWith(".jar");
         }
      });
      return jars;
   }

   public static int getModuleCount()
   {
      return getModuleFiles().length;
   }

   public static String[] getModuleNames()
   {
      File[] jars = getModuleFiles();
      String[] result = new String[jars.length];
      for(int i=0; i<jars.length; i++)
      {
         result[i] = jars[i].getName().substring(0, jars[i].getName().lastIndexOf("."));
      }
      return result;
   }

   public static String[] getModuleFileNames()
   {
      File[] jars = getModuleFiles();
      String[] result = new String[jars.length];
      for(int i=0; i<jars.length; i++)
      {
         result[i] = jars[i].getName();
      }
      return result;
   }


}
