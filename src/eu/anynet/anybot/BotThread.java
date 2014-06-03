/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot;

import static eu.anynet.anybot.AnyBot.properties;
import eu.anynet.anybot.bot.Bot;
import eu.anynet.anybot.bot.ChatEvent;
import eu.anynet.anybot.bot.ChatMessage;
import eu.anynet.anybot.bot.IRCCommand;
import eu.anynet.anybot.bot.Module;
import eu.anynet.anybot.bot.ThreadPipeEndpoint;
import eu.anynet.anybot.bot.ThreadPipes;
import eu.anynet.java.util.CommandLineEvent;
import eu.anynet.java.util.CommandLineListener;
import eu.anynet.java.util.CommandLineParser;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jibble.pircbot.IrcException;

/**
 *
 * @author sim
 */
public class BotThread extends Thread {

// http://en.wikipedia.org/wiki/List_of_Internet_Relay_Chat_commands

   private Bot bot;
   private final ThreadPipes pipes;
   private final ArrayList<String> joinedchannels;
   private final Network network;
   private ArrayList<IRCCommand> startupcommands;

   public BotThread(Network network) throws IOException
   {
      this.pipes = new ThreadPipes();
      this.joinedchannels = new ArrayList<>();
      this.network = network;
      this.startupcommands = new ArrayList<>();
   }

   public void addStartupCommand(IRCCommand cmd)
   {
      this.startupcommands.add(cmd);
   }

   public ThreadPipeEndpoint getPipeEndpoint()
   {
      return this.pipes.getOutsideEndpoint();
   }

   private String readPipeLine() throws IOException, InterruptedIOException
   {
      return this.pipes.getInsideEndpoint().receive();
   }

   private void writePipeLine(String message)
   {
      try {
         this.pipes.getInsideEndpoint().send(message+"\n");
      } catch (IOException ex) {
         Logger.getLogger(BotThread.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   @Override
   public void run()
   {
      try
      {
         final BotThread me = this;
         this.bot = new Bot(this.network.getBotIdent(), this.network.getBotRealname(), "anybot-2.0.0");

         // http://www.informatik-forum.at/showthread.php?66277-Java-Plugin-System-mit-jar-Dateien
         String modulefolder = AnyBot.properties.get("fs.execdir")+"modules"+File.separator;
         File[] jars = new File(modulefolder).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
               return pathname.getName().endsWith(".jar");
            }
         });

         if(jars!=null && jars.length>0)
         {
            for(File jar : jars)
            {
               try {
                  URLClassLoader loader = URLClassLoader.newInstance(new URL[] { jar.toURI().toURL() });
                  ResourceBundle props = ResourceBundle.getBundle("anybotmodule", Locale.getDefault(), loader);
                  final String isubClassName = props.getString("anbot.module.module");
                  Module sub = (Module) loader.loadClass(isubClassName).newInstance();
                  sub.mergeProperties(AnyBot.properties);
                  this.bot.addModule(sub);
                  sub.launch();
                  me.writePipeLine("Load module "+jar.getName());
               }
               catch(MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex)
               {
                  me.writePipeLine("Load of module "+jar.getName()+" failed: "+ex.getMessage());
               }
            }
         }
         else
         {
            me.writePipeLine("No modules found.");
         }



         // TODO: Put this in a module class
         this.bot.addModule(new Module() {
            @Override
            public void onConnect(ChatEvent ev) {
               me.writePipeLine("Connected!");
               me.bot.changeNick(me.network.getBotNickname());

               for(String channel : joinedchannels)
               {
                  me.writePipeLine("Join "+channel);
                  ev.getBot().joinChannel(channel);
               }

               for(IRCCommand cmd : startupcommands)
               {
                  me.bot.sendRawLineViaQueue(cmd.buildRawCommand());
               }

            }
            @Override
            public void onInvite(ChatMessage msg) {
               if(msg.getBot().getNick().equals(msg.getMessage()))
               {
                  String chan = msg.getChannel();
                  String source = msg.getNick();

                  if(msg.getHost().equals("sim4000.off.users.iZ-smart.net"))
                  {
                     msg.getBot().joinChannel(chan);
                  }
                  else
                  {
                     msg.respondNotice("Access denied!");
                  }
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

         this.bot.addModule(new Module() {
            @Override
            public void onMessage(ChatMessage msg)
            {
               if(msg.isBotAsked() && msg.count()>1 && msg.get(1).equalsIgnoreCase("version"))
               {
                  msg.respond(properties.get("versionstring"));
               }
            }
         });

         this.bot.enableAutoReconnect();
         this.bot.connect(network.getHost());

         CommandLineParser parser = new CommandLineParser();

         parser.addCommandLineListener(new CommandLineListener("^join") {
            @Override
            public void handleCommand(CommandLineEvent e) {
               String chan = e.get(1);
               me.writePipeLine("Join "+chan);
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

         parser.addCommandLineListener(new CommandLineListener("^msg") {
            @Override
            public void handleCommand(CommandLineEvent e) {
               bot.sendMessage(e.get(1), e.get(2, -1, " "));
            }
         });

         parser.addCommandLineListener(new CommandLineListener("^raw") {
            @Override
            public void handleCommand(CommandLineEvent e) {
               bot.sendRawLineViaQueue(e.get(1, -1, " "));
            }
         });

         while(true)
         {
            parser.handleCommandLine(this.readPipeLine());
         }

      }
      catch(InterruptedIOException ex)
      {
         this.writePipeLine("Thread exited.");
         this.bot.disconnect();
         this.bot.dispose();
      }
      catch (IOException | IrcException ex)
      {
         this.writePipeLine("Error: "+ex.getMessage());
      }
   }



}
