/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.java.util;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author sim
 */
abstract public class Serializable
{

   private File serializerfile = null;


   public void setSerializerFile(File f)
   {
      this.serializerfile = f;
   }

   public File getSerializerFile()
   {
      if(this.serializerfile==null)
      {
         throw new IllegalArgumentException("File not defined");
      }
      return this.serializerfile;
   }

   public void serialize() throws JAXBException
   {
      if(this.serializerfile==null)
      {
         throw new IllegalArgumentException("File not defined");
      }

      // Create marshaller
      // @see http://www.mkyong.com/java/jaxb-hello-world-example/
      JAXBContext context = JAXBContext.newInstance(this.getClass());
      Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

      // Write to System.out
      m.marshal(this, this.serializerfile);

   }

   public static Object unserialize(File file)
   {
      AXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
   }



}
