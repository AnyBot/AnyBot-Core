/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.anynet.java.util;

import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBException;

/**
 *
 * @author perry
 * @param <T> the classtype
 */
public abstract class Serializable<T extends Serializable>
{

   private Serializer<T> serializer;
   private T obj;
   private Class<T> classtype;

   protected void initSerializer(T obj, Class<T> classtype)
   {
      this.obj = obj;
      this.classtype = classtype;
      this.serializer = new Serializer<>(classtype);
   }

   public File serialize()
   {
      try
      {
         this.serializer.serialize(this.obj);
         return this.serializer.getSerializerFile();
      }
      catch(JAXBException | IOException | IllegalArgumentException ex)
      {
         return null;
      }
   }

   public Serializer<T> createSerializer(File serializerfile)
   {
      Serializer<T> newserializer = new Serializer<>(this.classtype);
      newserializer.setSerializerFile(serializerfile);
      return newserializer;
   }

   public String getSerializerFileName()
   {
      return null;
   }

   public String getSerializerPraefix()
   {
      return null;
   }

}
