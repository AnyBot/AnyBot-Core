/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.java.util;

/**
 *
 * @author sim
 */
public class SaveBoolean {

   private boolean bool;

   public SaveBoolean(boolean b)
   {
      this.bool = b;
   }

   public void setTrue()
   {
      this.bool = true;
   }

   public boolean isTrue()
   {
      return (this.bool==true);
   }

   public void setFalse()
   {
      this.bool = false;
   }

   public boolean isFalse()
   {
      return (this.bool==false);
   }

   public boolean get()
   {
      return this.bool;
   }

   public void set(boolean b)
   {
      this.bool = b;
   }

}
