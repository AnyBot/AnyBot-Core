/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot;

import eu.anynet.anybot.bot.BotThread;
import eu.anynet.anybot.bot.Network;
import eu.anynet.anybot.bot.NetworkSettingsStore;
import eu.anynet.anybot.bot.ThreadManager;
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
import javax.xml.bind.JAXBException;

/**
 *
 * @author sim
 */
public class AnyBot
{

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
      final ThreadManager pool = new ThreadManager();
      final SaveBoolean isRunning = new SaveBoolean(true);

      parser.addCommandLineListener(new CommandLineListener("^start") {
         @Override
         public void handleCommand(CommandLineEvent e) {
            String host = e.get(1);
            try {
               if(networks.getNetworkKeys().contains(host))
               {
                  Network network = networks.getNetwork(host);
                  network.getBotThread().start();
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
            try {
               networks.getNetwork(e.get(1)).getBotThread().interrupt();
            } catch (IOException ex) {
               Logger.getLogger(AnyBot.class.getName()).log(Level.SEVERE, null, ex);
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



      //--> Test stuff... Ignore it...

      /*
      Wizard wiz = new Wizard();
      wiz.addQuestion(new WizardQuestion("Hostname (iz-smart.net)", WizardQuestion.REGEX_ANY));
      wiz.addQuestion(new WizardQuestion("Port (6667)", WizardQuestion.REGEX_INTEGER));
      wiz.addQuestion(new WizardQuestion("Nickname", WizardQuestion.REGEX_IRCNICK));

      wiz.startWizard();
      */

      /*
      NetworkSettings ns_coolirc = new NetworkSettings();
      ns_coolirc.setAutostart(true);
      ns_coolirc.setBotIdent("anybot");
      ns_coolirc.setBotNickname("AnyBot|dev");
      ns_coolirc.setBotRealname("AnyBot <b>Development</b> Instance");
      ns_coolirc.setHost("a-cool-irc.net");
      ns_coolirc.setPort(1337);
      ns_coolirc.setSsl(true);

      IRCCommand cmd1 = new IRCCommand();
      cmd1.setType(IRCCommand.CommandType.USER);
      cmd1.setTarget("NickServ");
      cmd1.setCommand("IDENTIFY huhu123");

      ns_coolirc.addAfterConnectCommand(cmd1);

      NetworkSettings ns_izsmart = new NetworkSettings();
      ns_izsmart.setBotIdent("anybot");
      ns_izsmart.setBotNickname("AnyBot|izdev");
      ns_izsmart.setBotRealname("iz-smart development instance");
      ns_izsmart.setHost("iz-smart.net");
      ns_izsmart.setPort(6667);

      ns_izsmart.addBeforeDisconnectCommand(cmd1);

      NetworkSettingsStore store = new NetworkSettingsStore();
      store.addNetwork("anynet", ns_coolirc);
      store.addNetwork("izsmart", ns_izsmart);

      try {
         File nsfile = store.serialize();
         NetworkSettingsStore newsettings = new NetworkSettingsStore().createSerializer(nsfile).unserialize();

         //NetworkSettings newsettings = serr.unserialize();
         System.out.println("Networks: "+StringUtils.join(newsettings.getNetworkKeys().toArray(), ", "));

      } catch (JAXBException ex) {
         Logger.getLogger(AnyBot.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
         Logger.getLogger(AnyBot.class.getName()).log(Level.SEVERE, null, ex);
      }
      */

      /*
      try {
      JAXBContext context = JAXBContext.newInstance(NetworkSettings.class);
      Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

      // Write to System.out
      m.marshal(ns, System.out);

      }
      catch(JAXBException ex)
      {
      ex.printStackTrace();
      }
      */

      /*
      private Plugin createPluginInstance(boolean override) throws Exception {
      String pluginname = this.configPluginlist.get(this.pluginList.getSelectedIndex()).get(0);

      if(!this.pluginInstances.containsKey(pluginname) || override) {
      Class myPlugin = Class.forName("comicdownloader.plugins."+pluginname);
      Object o = myPlugin.getConstructor().newInstance();
      myPlugin.getMethod("setGui", new Class[]{GUI.class}).invoke(o, this);
      this.pluginInstances.put(pluginname, (Plugin)o);
      return (Plugin)o;
      } else {
      return this.pluginInstances.get(pluginname);
      }
      }
      */
      /*
      List<String> classes = PackageScanner.listClassesInPackage("eu.anynet.anybot.bot");
      for(String cl : classes)
      {
      System.out.println(cl);
      }
      */

   }

}
