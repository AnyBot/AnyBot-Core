/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot;

import eu.anynet.anybot.bot.ModuleUtils;
import eu.anynet.anybot.bot.Network;
import eu.anynet.anybot.bot.NetworkSettingsStore;
import eu.anynet.anybot.wizard.Wizard;
import eu.anynet.anybot.wizard.WizardQuestion;
import eu.anynet.anybot.wizard.WizardQuestionFlag;
import eu.anynet.java.util.CommandLineEvent;
import eu.anynet.java.util.CommandLineListener;
import eu.anynet.java.util.CommandLineModuleBase;
import eu.anynet.java.util.CommandLineParser;
import eu.anynet.java.util.Properties;
import static eu.anynet.java.util.Properties.properties;
import eu.anynet.java.util.SaveBoolean;
import eu.anynet.java.util.Serializer;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author sim
 */
public class AnyBot
{

   public static final String BASEVERSION = "anybot-1.4";


   public void begin()
   {

      System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");

      // Version info
      System.out.println("Welcome to AnyBot!");
      System.out.println("Version: "+properties.get("versionstring"));
      System.out.println();

      // Directories
      System.out.println("Settings folder:        "+properties.get("fs.settings"));
      System.out.println("Module folder:          "+properties.get("fs.execdir")+"modules"+File.separator);
      System.out.println("Current working folder: "+properties.get("fs.cwd"));
      System.out.println("Execution folder:       "+properties.get("fs.execdir"));
      System.out.println();

      // Set module folder
      ModuleUtils.setModuleFolder(properties.get("fs.execdir")+"modules"+File.separator);
      ModuleUtils.setSettingsFolder(properties.get("fs.settings")+"modules"+File.separator);

      // Available modules
      String modules = StringUtils.join(ModuleUtils.getModuleNames(), ", ");
      System.out.println(ModuleUtils.getModuleCount()+" modules found: "+ (modules==null ? "No modules found!" : modules));

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

      CommandLineModuleBase.loadAll(parser, new Object[] { networks, isRunning });

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
            wiz.addQuestion(new WizardQuestionFlag("debugchannel", "DebugChannel").setDefault(network.getDebugChannel()==null ? "" : network.getDebugChannel()));
            Properties result = wiz.startWizard();

            network.setAutostart(result.getBoolean("autostart"));
            network.setHost(result.get("host"));
            network.setPort(result.getInt("port"));
            network.setSsl(result.getBoolean("ssl"));
            network.setBotNickname(result.get("nick"));
            network.setBotIdent(result.get("ident"));
            network.setBotRealname(result.get("realname"));
            network.setDebugChannel(result.get("debugchannel"));

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

      parser.addCommandLineListener(new CommandLineListener("^version") {
         @Override
         public void handleCommand(CommandLineEvent e)
         {
            System.out.println(properties.get("versionstring"));
         }
      });

      System.out.println();
      System.out.println("AnyBot shell launched, enjoy!");

      Scanner in = new Scanner(System.in);
      while(isRunning.get())
      {
         System.out.print("> ");
         parser.handleCommandLine(in.nextLine());
         String[] mq = parser.consumeMessageQueue();
         if(mq!=null && mq.length>0)
         {
            System.out.print("Usage: ");
            System.out.println(StringUtils.join(mq, "\n"));
         }
      }

      System.out.println("\nBye!");
   }


   /**
    * @param args the command line arguments
    */
   public static void main(String[] args)
   {

      //--> Copy version info to properties
      ResourceBundle vprops = ResourceBundle.getBundle("version", Locale.getDefault());
      properties.set("buildnumber", vprops.getString("BUILDNUMBER"));
      properties.set("builddate", vprops.getString("BUILDDATE"));
      properties.set("version", BASEVERSION);
      properties.set("versionbuild", properties.get("version")+" build "+properties.get("buildnumber"));
      properties.set("versionstring", properties.get("versionbuild")+" (compiled on "+properties.get("builddate")+")");

      //--> Properties
      properties.set("fs.settings", System.getProperty("user.home")+File.separator+".AnyBot"+File.separator);
      properties.set("fs.cwd", System.getProperty("user.dir")+File.separator);

      File f = (new File(AnyBot.class.getProtectionDomain().getCodeSource().getLocation().getPath()));
      if(f.isFile()) { f = f.getParentFile(); }
      properties.set("fs.execdir", f.getAbsolutePath()+File.separator);

      //--> Set serializer default folder
      Serializer.setDefaultFolder(properties.get("fs.settings"));

      //--> Start the bot master thread
      AnyBot anybot = new AnyBot();
      anybot.begin();

   }

}
