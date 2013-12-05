/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.bot;

import eu.anynet.java.util.CommandLineEvent;
import eu.anynet.java.util.CommandLineListener;
import eu.anynet.java.util.CommandLineParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sim
 */
public class BotThread extends Thread {

   private Bot bot;
   private ThreadPipes pipes;
   private ArrayList<String> joinedchannels;

   public BotThread() throws IOException
   {
      this.pipes = new ThreadPipes();
      this.joinedchannels = new ArrayList<>();
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
         this.bot.enableAutoReconnect();

         this.bot.addModule(new Module() {
            @Override
            public void onConnect(ChatEvent ev) {
               System.out.println("["+ev.getBot().getServer()+"] Connected!");
               for(String channel : joinedchannels)
               {
                  System.out.println("["+ev.getBot().getServer()+"] Join "+channel);
                  ev.getBot().joinChannel(channel);
               }
            }
         });

         this.bot.addModule(new Module() {
            @Override
            public void onInvite(ChatMessage msg) {
               if(msg.getBot().getNick().equals(msg.getMessage()))
               {
                  msg.getBot().joinChannel(msg.getChannel());
               }
            }
         });

         this.bot.connect(host);

         CommandLineParser parser = new CommandLineParser();

         parser.addCommandLineListener(new CommandLineListener() {
            @Override
            public void handleCommand(CommandLineEvent e) {
               if(e.get(0).equals("join"))
               {
                  bot.joinChannel(e.get(1));
                  joinedchannels.add(e.get(1));
               }
            }
         });

         parser.addCommandLineListener(new CommandLineListener() {

            @Override
            public void handleCommand(CommandLineEvent e) {
               if(e.get(0).equals("part"))
               {
                  bot.partChannel(e.get(1));
                  joinedchannels.remove(e.get(1));
               }
            }
         });

         parser.addCommandLineListener(new CommandLineListener() {

            @Override
            public void handleCommand(CommandLineEvent e) {
               if(e.get(0).equals("quit"))
               {
                  bot.disableAutoReconnect();
                  bot.quitServer();
               }
            }
         });

         while(true)
         {
            parser.handleCommandLine(this.readPipeLine());
         }

      } catch (Exception ex) {
         Logger.getLogger(BotThread.class.getName()).log(Level.SEVERE, null, ex);
      }
   }



}
