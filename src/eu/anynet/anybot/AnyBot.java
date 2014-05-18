/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot;

import eu.anynet.anybot.bot.BotThread;
import eu.anynet.anybot.bot.ThreadManager;
import eu.anynet.java.util.CommandLineEvent;
import eu.anynet.java.util.CommandLineListener;
import eu.anynet.java.util.CommandLineParser;
import eu.anynet.java.util.SaveBoolean;
import eu.anynet.java.util.Serializer;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author sim
 */
public class AnyBot {


   public void begin()
   {
      final CommandLineParser parser = new CommandLineParser();
      final ThreadManager pool = new ThreadManager();
      final SaveBoolean isRunning = new SaveBoolean(true);


      parser.addCommandLineListener(new CommandLineListener("^start") {
         @Override
         public void handleCommand(CommandLineEvent e) {
            String host = e.get(1);
            try {
               BotThread newthread = new BotThread();
               newthread.setName(host);
               pool.add(newthread);
               pool.start(host);
            } catch(IOException ex) {
               System.out.println("["+host+"] Could not start: "+ex.getMessage());
            }
         }
      });

      parser.addCommandLineListener(new CommandLineListener("^stop") {
         @Override
         public void handleCommand(CommandLineEvent e) {
            pool.kill(e.get(1));
         }
      });

      parser.addCommandLineListener(new CommandLineListener("^exit") {
         @Override
         public void handleCommand(CommandLineEvent e) {
            pool.killAll();
            isRunning.setFalse();
         }
      });

      parser.addCommandLineListener(new CommandLineListener("^send") {
         @Override
         public void handleCommand(CommandLineEvent e) {
            pool.send(e.get(1), e.get(2, -1)+"\n");
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
      // Start the bot master thread
      AnyBot anybot = new AnyBot();
      anybot.begin();

      // Test stuff... Ignore it...
      
      /*
      Wizard wiz = new Wizard();
      wiz.addQuestion(new WizardQuestion("Hostname (iz-smart.net)", WizardQuestion.REGEX_ANY));
      wiz.addQuestion(new WizardQuestion("Port (6667)", WizardQuestion.REGEX_INTEGER));
      wiz.addQuestion(new WizardQuestion("Nickname", WizardQuestion.REGEX_IRCNICK));

      wiz.startWizard();
      */
      
      //--> Set serializer default folder
      Serializer.setDefaultFolder(System.getProperty("user.home")+File.separator+".AnyBot"+File.separator);

      /*
      NetworkSettings ns = new NetworkSettings();
      ns.setAutostart(true);
      ns.setBotIdent("anybot");
      ns.setBotNickname("AnyBot|dev");
      ns.setBotRealname("AnyBot <b>Development</b> Instance");
      ns.setHost("a-cool-irc.net");
      ns.setPort(1337);
      ns.setSsl(true);

      IRCCommand cmd1 = new IRCCommand();
      cmd1.setType(IRCCommand.CommandType.USER);
      cmd1.setTarget("NickServ");
      cmd1.setCommand("IDENTIFY huhu123");

      ns.addAfterConnectCommand(cmd1);
      
      try {
         File nsfile = ns.serialize();
         
         NetworkSettings newsettings = new NetworkSettings().createSerializer(nsfile).unserialize();
         
         //NetworkSettings newsettings = serr.unserialize();
         System.out.println("Hostname: "+newsettings.getHost());
         System.out.println("Autostart: "+newsettings.isAutostartEnabled());
         System.out.println("SSL: "+newsettings.isSsl());
         
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
