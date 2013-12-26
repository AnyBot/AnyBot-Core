/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot.bot;

import eu.anynet.anybot.module.TimerDemo;
import eu.anynet.java.util.CommandLineEvent;
import eu.anynet.java.util.CommandLineListener;
import eu.anynet.java.util.CommandLineParser;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import org.jibble.pircbot.IrcException;

/**
 *
 * @author sim
 */
public class BotThread extends Thread {

   private Bot bot;
   private final ThreadPipes pipes;
   private final ArrayList<String> joinedchannels;

   public BotThread() throws IOException
   {
      this.pipes = new ThreadPipes();
      this.joinedchannels = new ArrayList<>();
   }

   public ThreadPipeEndpoint getPipeEndpoint()
   {
      return this.pipes.getOutsideEndpoint();
   }

   private String readPipeLine() throws IOException, InterruptedIOException
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

         // TODO: Use reflection to add modules by xml dynamicly
         this.bot.addModule(new TimerDemo());

         // TODO: Put this in a module class
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
            @Override
            public void onInvite(ChatMessage msg) {
               if(msg.getBot().getNick().equals(msg.getMessage()))
               {
                  String chan = msg.getChannel();
                  msg.getBot().joinChannel(chan);
               }
            }
            @Override
            public void onJoin(ChatMessage msg) {
               if(msg.getNick().equals(msg.getBot().getNick()) && !joinedchannels.contains(msg.getChannel())) {
                  joinedchannels.add(msg.getChannel());
               }
            }
            @Override
            public void onPart(ChatMessage msg) {
               if(msg.getNick().equals(msg.getBot().getNick()) && joinedchannels.contains(msg.getChannel())) {
                  joinedchannels.remove(msg.getChannel());
               }
            }
            @Override
            public void onKick(ChatMessage msg) {
               if(msg.getRecipient().equals(msg.getBot().getNick()) && joinedchannels.contains(msg.getChannel()))
               {
                  joinedchannels.remove(msg.getChannel());
               }
            }
         });

         this.bot.enableAutoReconnect();
         this.bot.connect(host);

         CommandLineParser parser = new CommandLineParser();

         parser.addCommandLineListener(new CommandLineListener("^join") {
            @Override
            public void handleCommand(CommandLineEvent e) {
               String chan = e.get(1);
               bot.joinChannel(chan);
            }
         });

         parser.addCommandLineListener(new CommandLineListener("^part") {
            @Override
            public void handleCommand(CommandLineEvent e) {
               String chan = e.get(1);
               bot.partChannel(chan);
            }
         });

         parser.addCommandLineListener(new CommandLineListener("^quit") {
            @Override
            public void handleCommand(CommandLineEvent e) {
               bot.disableAutoReconnect();
               bot.quitServer();
            }
         });

         while(true)
         {
            parser.handleCommandLine(this.readPipeLine());
         }

      }
      catch(InterruptedIOException ex)
      {
         System.out.println("["+this.bot.getServer()+"] Thread exited.");
         this.bot.disconnect();
         this.bot.dispose();
      }
      catch (IOException | IrcException ex)
      {
         System.out.println("["+this.bot.getServer()+"] Error: "+ex.getMessage());
      }
   }



}
