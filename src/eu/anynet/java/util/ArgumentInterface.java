/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.java.util;

/**
 *
 * @author sim
 */
public interface ArgumentInterface {

   public int count();

   public String get();

   public String get(int i);

   public String get(int start, int end, String glue);

   public String get(int start, int end);

}
