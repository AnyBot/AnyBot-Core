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
public interface DisconnectListener extends EventListener {

   public void handleDisconnect(Bot bot);

}
