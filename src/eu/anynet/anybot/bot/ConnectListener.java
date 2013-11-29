/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.bot;

import java.util.EventListener;

/**
 *
 * @author sim
 */
public interface ConnectListener extends EventListener {

   public void handleConnect(Bot bot);

}
