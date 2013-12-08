/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.java.util;

/**
 *
 * @author sim
 */
abstract public class TimerTask {

   private boolean isstarted;
   private Thread worker;

   public TimerTask(final long millis)
   {
      this.isstarted = false;
      final TimerTask me = this;
      this.worker = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               while(true)
               {
                  me.doWork();
                  Thread.sleep(millis);
               }
            }
            catch(InterruptedException ex)
            {
               // Work done!
            }
         }
      };
   }

   public void start()
   {
      if(this.isstarted)
      {
         throw new IllegalStateException("Thread was already started");
      }

      this.isstarted = true;
      this.worker.start();
   }

   public void stop()
   {
      this.worker.interrupt();
   }

   public void doWork()
   {

   }

}
