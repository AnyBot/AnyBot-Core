/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.bot;

import java.io.IOException;
import java.io.PipedReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

/**
 *
 * @author sim
 */
public class BotThread extends Thread {

   private Bot bot;
   private ThreadPipes pipes;

   public BotThread() throws IOException
   {
      this.pipes = new ThreadPipes();
   }

   public ThreadPipeEndpoint getPipeEndpoint()
   {
      return this.pipes.getOutsideEndpoint();
   }

   private String readPipeLine() throws IOException
   {
      return this.pipes.getInsideEndpoint().receive();
   }

   private void writePipeLine(String message) throws IOException
   {
      this.pipes.getInsideEndpoint().send(message);
   }

   @Override
   public void run()
   {
      try {
         String host = this.getName();
         this.bot = new Bot();
         this.bot.connect(host);

         while(true)
         {
            System.out.println("["+this.getName()+" via pipe] "+this.readPipeLine());
         }

      } catch (Exception ex) {
         Logger.getLogger(BotThread.class.getName()).log(Level.SEVERE, null, ex);
      }
   }



}
