/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.java.util;

import java.util.EventListener;

/**
 *
 * @author sim
 */
public interface CommandLineListener extends EventListener {

   public void handleCommand(CommandLineEvent e);

}
