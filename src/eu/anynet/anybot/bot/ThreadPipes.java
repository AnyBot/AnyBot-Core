/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.bot;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

/**
 *
 * @author sim
 * Source: http://howtodoinjava.com/2012/11/17/inter-thread-communication-using-piped-streams-in-java/
 */
public class ThreadPipes
{

   private PipedReader insidereader;
   private PipedWriter insidewriter;
   private PipedReader outsidereader;
   private PipedWriter outsidewriter;

   public ThreadPipes() throws IOException
   {
      this.insidereader = new PipedReader();
      this.outsidewriter = new PipedWriter();
      this.outsidewriter.connect(this.insidereader);

      this.outsidereader = new PipedReader();
      this.insidewriter = new PipedWriter();
      this.insidewriter.connect(this.outsidereader);
   }


   public ThreadPipeEndpoint getInsideEndpoint()
   {
      return new ThreadPipeEndpoint(this.insidereader, this.insidewriter);
   }


   public ThreadPipeEndpoint getOutsideEndpoint()
   {
      return new ThreadPipeEndpoint(this.outsidereader, this.outsidewriter);
   }


}
