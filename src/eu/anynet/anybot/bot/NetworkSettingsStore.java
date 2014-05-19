/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.anynet.anybot.bot;

import eu.anynet.java.util.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author perry
 */
@XmlRootElement(name = "NetworkSettingsStore")
@XmlAccessorType(XmlAccessType.FIELD)
public class NetworkSettingsStore extends Serializable<NetworkSettingsStore>
{

   @XmlElementWrapper(name = "Networks")
   @XmlElement(name = "NetworkSettings")
   private HashMap<String, Network> networks;


   public NetworkSettingsStore()
   {
      this.initSerializer(this, NetworkSettingsStore.class);
      this.networks = new HashMap<>();
   }


   public void addNetwork(String key, Network network)
   {
      if(this.networks.keySet().contains(key))
      {
         throw new IllegalArgumentException("Key already exist");
      }
      this.networks.put(key, network);
   }


   public void removeNetwork(String key)
   {
      this.networks.remove(key);
   }


   public boolean exists(String key)
   {
      return this.networks.keySet().contains(key);
   }


   public Network getNetwork(String key)
   {
      return this.networks.get(key);
   }


   public ArrayList<String> getNetworkKeys()
   {
      ArrayList<String> newlist = new ArrayList<>();
      newlist.addAll(this.networks.keySet());
      return newlist;
   }


   @Override
   public String getSerializerFileName()
   {
      return "networks.xml";
   }



}
