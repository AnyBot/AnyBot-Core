/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.java.util;

import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author sim
 * @param <T> the classtype
 */
public class Serializer<T extends Serializable>
{
   
   private static String defaultFolder;
   
   private String serializerfiletpl = null;
   private File serializerfile = null;
   private final Class<T> classtype;
   
   
   /**
    * Initialize a Serializer with classtype and filename praefix
    * @param classtype The class type
    * @throws IllegalArgumentException 
    */
   public Serializer(Class<T> classtype) throws IllegalArgumentException
   {
      if(defaultFolder==null)
      {
         throw new IllegalArgumentException("No default serializer folder defined");
      }
      
      this.classtype = classtype;
      this.serializerfiletpl = defaultFolder+"{name}_"+classtype.getName()+".xml";
   }
   
   
   /**
    * Define default folder for serialized objects
    * @param path default folder, will automatic created
    */
   public static void setDefaultFolder(String path)
   {
      defaultFolder = path;
   }
   
   
   /**
    * Get the default folder
    * @return the folder path
    */
   public static String getDefaultFolder()
   {
      return defaultFolder;
   }
   
   
   /**
    * Get the serializer file
    * @return the serializer file
    */
   public File getSerializerFile()
   {
      return this.serializerfile;
   }
   
   
   /**
    * Check for serialize file
    * @return ok or not
    */
   public boolean isReadyForUnserialize()
   {
      if(this.serializerfile!=null && this.serializerfile.exists())
      {
         return true;
      }
      return false;
   }
   
   
   /**
    * Override the default serialize file
    * @param serializerfile the xml file
    */
   public void setSerializerFile(File serializerfile)
   {
      this.serializerfile = serializerfile;
   }
   
   
   /**
    * Normalize filename
    * @param name The name
    * @return The normalized name
    */
   public String normalizeName(String name)
   {
      return Regex.replace("[^A-Za-z\\-0-9]", name, "-");
   }

   
   /**
    * Serialize!
    * @param obj the object
    * @throws JAXBException
    * @throws IOException 
    */
   public void serialize(T obj) throws JAXBException, IOException, IllegalArgumentException
   {
      String fullname = obj.getSerializerFileName();
      String namepraefix = obj.getSerializerPraefix();
      
      // Build filename
      if(fullname!=null)
      {
         if(fullname.contains(File.separator))
         {
            this.serializerfile = new File(fullname);
         }
         else
         {
            String tmp = new File(this.serializerfiletpl).getParentFile().getAbsolutePath();
            this.serializerfile = new File(tmp+File.separator+fullname);
         }
      }
      else if(namepraefix!=null)
      {
         this.serializerfile = new File(this.serializerfiletpl.replace("{name}", this.normalizeName(namepraefix)));
      }
      else
      {
         throw new IllegalArgumentException("No serializer name/praefix defined");
      }
      
      // Create target folder
      if(!this.serializerfile.getParentFile().exists())
      {
         if(!this.serializerfile.getParentFile().mkdirs())
         {
            throw new IOException("Could not create serializer folder");
         }
      }

      // Create marshaller
      // @see http://www.mkyong.com/java/jaxb-hello-world-example/
      JAXBContext context = JAXBContext.newInstance(obj.getClass());
      Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      
      // Write
      m.marshal(obj, this.serializerfile);
   }

   
   /**
    * Unserialize!
    * @return The object
    */
   public T unserialize()
   {
      try 
      {
         JAXBContext jaxbContext = JAXBContext.newInstance(this.classtype);
         Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
         return (T)jaxbUnmarshaller.unmarshal(this.serializerfile);
      } 
      catch(Exception ex)
      {
         return null;
      }
   }



}
