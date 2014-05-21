/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot;

import eu.anynet.anybot.bot.BotThread;
import eu.anynet.anybot.bot.Network;
import eu.anynet.anybot.bot.NetworkSettingsStore;
import eu.anynet.anybot.wizard.Wizard;
import eu.anynet.anybot.wizard.WizardQuestion;
import eu.anynet.anybot.wizard.WizardQuestionFlag;
import eu.anynet.java.util.CommandLineEvent;
import eu.anynet.java.util.CommandLineListener;
import eu.anynet.java.util.CommandLineParser;
import eu.anynet.java.util.Properties;
import eu.anynet.java.util.SaveBoolean;
import eu.anynet.java.util.Serializer;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sim
 */
public class AnyBot
{

   public static final String VERSION = "anybot-0.1.1";
   public static final Properties properties = new Properties();


   public void begin()
   {

      // Load Network store
      File networkpoolfile = new File(properties.get("fs.settings")+"networks.xml");
      Serializer<NetworkSettingsStore> serializer = new NetworkSettingsStore().createSerializer(networkpoolfile);
      final NetworkSettingsStore networks;
      if(serializer.isReadyForUnserialize()) {
         networks = serializer.unserialize();
      } else {
         networks = new NetworkSettingsStore();
      }

      // Command line parser
      final CommandLineParser parser = new CommandLineParser();
      final SaveBoolean isRunning = new SaveBoolean(true);

      parser.addCommandLineListener(new CommandLineListener("^start") {
         @Override
         public void handleCommand(CommandLineEvent e) {
            String host = e.get(1);
            try {
               if(networks.getNetworkKeys().contains(host))
               {
                  Network network = networks.getNetwork(host);
                  if(!network.isRunning())
                  {
                     network.start();
                  }
                  else
                  {
                     System.out.println("["+host+"] Network already running.");
                  }
               }
               else
               {
                  System.out.println("["+host+"] Network definition not found.");
               }
            } catch(IOException ex) {
               System.out.println("["+host+"] Could not start: "+ex.getMessage());
            }
         }
      });

      parser.addCommandLineListener(new CommandLineListener("^stop") {
         @Override
         public void handleCommand(CommandLineEvent e) {
            String host = e.get(1);
            if(networks.getNetworkKeys().contains(host))
            {
               Network network = networks.getNetwork(host);
               if(network.isRunning())
               {
                  network.stop();
               }
               else
               {
                  System.out.println("["+host+"] Network not running.");
               }
            }
            else
            {
               System.out.println("["+host+"] Network definition not found.");
            }
         }
      });

      parser.addCommandLineListener(new CommandLineListener("^exit") {
         @Override
         public void handleCommand(CommandLineEvent e)
         {
            for(String netname : networks.getNetworkKeys())
            {
               try {
                  BotThread thr = networks.getNetwork(netname).getBotThread();
                  if(thr.isAlive())
                  {
                     thr.interrupt();
                  }
               } catch (IOException ex) {
                  System.out.println("["+netname+"] Could not interrupt: "+ex.getMessage());
               }
            }
            isRunning.setFalse();
         }
      });

      parser.addCommandLineListener(new CommandLineListener("^send") {
         @Override
         public void handleCommand(CommandLineEvent e) {
            try {
               networks.getNetwork(e.get(1)).getBotThread().getPipeEndpoint().send(e.get(2, -1)+"\n");
            } catch (IOException ex) {
               Logger.getLogger(AnyBot.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      });

      parser.addCommandLineListener(new CommandLineListener("^change") {
         @Override
         public void handleCommand(CommandLineEvent e)
         {
            if(e.count()<2)
            {
               return;
            }

            // Get or create network object
            boolean newnet = true;
            String netname = e.get(1);
            Network network = new Network();
            if(networks.exists(netname))
            {
               newnet=false;
               network = networks.getNetwork(netname);
            }

            // Run wizard
            Wizard wiz = new Wizard();
            wiz.addQuestion(new WizardQuestion("host", "Hostname").setDefault(network.getHost()));
            wiz.addQuestion(new WizardQuestion("port", "Port").setCheck(WizardQuestion.REGEX_INTEGER).setDefault(Integer.toString(network.getPort())));
            wiz.addQuestion(new WizardQuestionFlag("ssl", "SSL").setDefault(network.isSsl() ? "yes" : "no"));
            wiz.addQuestion(new WizardQuestion("nick", "Nickname").setCheck(WizardQuestion.REGEX_IRCNICK).setDefault(network.getBotNickname()));
            wiz.addQuestion(new WizardQuestion("ident", "Ident").setCheck(WizardQuestion.REGEX_IRCNICK).setDefault(network.getBotIdent()));
            wiz.addQuestion(new WizardQuestion("realname", "Realname").setDefault(network.getBotRealname()));
            wiz.addQuestion(new WizardQuestionFlag("autostart", "Autostart").setDefault(network.isAutostartEnabled() ? "yes" : "no"));
            Properties result = wiz.startWizard();

            network.setAutostart(result.getBoolean("autostart"));
            network.setHost(result.get("host"));
            network.setPort(result.getInt("port"));
            network.setSsl(result.getBoolean("ssl"));
            network.setBotNickname(result.get("nick"));
            network.setBotIdent(result.get("ident"));
            network.setBotRealname(result.get("realname"));

            if(newnet==true)
            {
               networks.addNetwork(netname, network);
            }
            networks.serialize();
         }
      });

      parser.addCommandLineListener(new CommandLineListener("^remove") {
         @Override
         public void handleCommand(CommandLineEvent e)
         {
            if(e.count()<2)
            {
               return;
            }
            String netname = e.get(1);
            networks.removeNetwork(netname);
            networks.serialize();
         }
      });

      parser.addCommandLineListener(new CommandLineListener("^list") {
         @Override
         public void handleCommand(CommandLineEvent e)
         {
            for(String netname : networks.getNetworkKeys())
            {
               System.out.println(netname+" ("+networks.getNetwork(netname).getHost()+")");
            }
         }
      });

      System.out.println("Welcome to the anybot shell!");
      Scanner in = new Scanner(System.in);
      while(isRunning.get())
      {
         System.out.print("> ");
         parser.handleCommandLine(in.nextLine());
      }

      System.out.println("\nBye!");
   }


   /**
    * @param args the command line arguments
    */
   public static void main(String[] args)
   {

      //--> Properties
      properties.set("fs.settings", System.getProperty("user.home")+File.separator+".AnyBot"+File.separator);

      //--> Set serializer default folder
      Serializer.setDefaultFolder(properties.get("fs.settings"));

      //--> Start the bot master thread
      AnyBot anybot = new AnyBot();
      anybot.begin();

   }

}
