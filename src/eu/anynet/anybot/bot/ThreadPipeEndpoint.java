/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.bot;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PipedReader;
import java.io.PipedWriter;

/**
 *
 * @author sim
 */
public class ThreadPipeEndpoint {

   private PipedWriter writer;
   private PipedReader reader;

   public ThreadPipeEndpoint(PipedReader reader, PipedWriter writer)
   {
      this.reader = reader;
      this.writer = writer;
   }

   public void send(String text) throws IOException
   {
      this.writer.write(text);
      this.writer.flush();
   }

   public String receive() throws IOException, InterruptedIOException
   {
      StringBuilder rtext = new StringBuilder();
      int c ;
      do {
         c = this.reader.read();
         if(c==10) break;
         rtext.append(((char)c));
      } while(true);
      return rtext.toString();
   }

}
