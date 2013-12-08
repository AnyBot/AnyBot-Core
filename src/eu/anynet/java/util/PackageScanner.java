/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.java.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author sim
 *
 * Grabbed from: http://ricardozuasti.com/2012/list-all-classes-in-a-package-even-from-a-jar-file/
 *
 */
public class PackageScanner {


   public static List listClassesInPackage(String packageName)
           throws ClassNotFoundException, IOException {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      assert classLoader != null;
      String path = packageName.replace('.', '/');
      Enumeration resources = classLoader.getResources(path);
      ArrayList<String> dirs = new ArrayList<>();
      while (resources.hasMoreElements()) {
         URL resource = (URL) resources.nextElement();
         dirs.add(URLDecoder.decode(resource.getFile(), "UTF-8"));
      }
      TreeSet<String> classes = new TreeSet<>();
      for (String directory : dirs) {
         classes.addAll(findClasses(directory, packageName));
      }
      ArrayList<String> classList = new ArrayList<>();
      for (String clazz : classes) {
         classList.add(Class.forName(clazz).getName());
      }
      return classList;
   }

   private static TreeSet findClasses(String path, String packageName) throws MalformedURLException, IOException {
      TreeSet classes = new TreeSet();
      if (path.startsWith("file:") && path.contains("!")) {
         String[] split = path.split("!");
         URL jar = new URL(split[0]);
         ZipInputStream zip = new ZipInputStream(jar.openStream());
         ZipEntry entry;
         while ((entry = zip.getNextEntry()) != null) {
            if (entry.getName().endsWith(".class")) {
               String className = entry.getName().replaceAll("[$].*", "").replaceAll("[.]class", "").replace('/', '.');
               if (className.startsWith(packageName)) {
                  classes.add(className);
               }
            }
         }
      }
      File dir = new File(path);
      if (!dir.exists()) {
         return classes;
      }
      File[] files = dir.listFiles();
      for (File file : files) {
         if (file.isDirectory()) {
            assert !file.getName().contains(".");
            classes.addAll(findClasses(file.getAbsolutePath(), packageName + "." + file.getName()));
         } else if (file.getName().endsWith(".class")) {
            String className = packageName + '.' + file.getName().replaceAll("[$].*", "").replaceAll("[.]class", "").replace('/', '.');
            classes.add(className);
         }
      }
      return classes;
   }


}
