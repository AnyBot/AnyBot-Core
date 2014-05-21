/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.java.twitter;

import eu.anynet.java.util.HTTPConnector;
import eu.anynet.java.util.Serializable;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author sim
 */
@XmlRootElement(name = "NetworkSettingsStore")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitterApiCredentials extends Serializable
{

   private String applicationApiKey;
   private String applicationApiSecret;
   private String bearerToken;

   public TwitterApiCredentials()
   {
      this(null, null);
   }

   public TwitterApiCredentials(String key, String secret)
   {
      this.initSerializer(this, TwitterApiCredentials.class);
      this.applicationApiKey = key;
      this.applicationApiSecret = secret;
      this.bearerToken = null;
   }

   public boolean isCredentialsAvailable()
   {
      return (this.applicationApiKey!=null && this.applicationApiKey.length()>0 &&
              this.applicationApiSecret!=null && this.applicationApiSecret.length()>0);
   }

   public String getBasicToken()
   {
      String cred = this.applicationApiKey+":"+this.applicationApiSecret;
      return Base64.encodeBase64String(cred.getBytes());
   }

   public boolean isBearerTokenAvailable()
   {
      return (this.bearerToken!=null && this.bearerToken.length()>0);
   }

   public boolean generateBearerToken()
   {
      if(!this.isBearerTokenAvailable())
      {
         HashMap<String,String> headers = new HashMap<>();
         headers.put("Authorization", "Basic "+this.getBasicToken());

         HashMap<String,String> params = new HashMap<>();
         params.put("grant_type", "client_credentials");

         HTTPConnector client = new HTTPConnector();
         String content = null;
         try {
            content = client.responseToString(client.doPost("https://api.twitter.com/oauth2/token", params, headers));
            JSONObject bearer_json = (JSONObject)JSONValue.parse(content);
            String access_token = bearer_json.get("access_token").toString();

            if(access_token!=null && access_token.length()>0)
            {
               this.setBearerToken(access_token);
               System.out.println("Created twitter access token");
               this.serialize();
               return true;
            }
         } catch(Exception ex) {
         } finally {
            client.close();
         }
      }
      return false;
   }

   public boolean invalidateBearerToken()
   {
      if(this.isBearerTokenAvailable())
      {
         HashMap<String,String> headers = new HashMap<>();
         headers.put("Authorization", "Basic "+this.getBasicToken());

         HashMap<String,String> params = new HashMap<>();
         params.put("access_token", this.getBearerToken());

         HTTPConnector client = new HTTPConnector();
         String content = null;
         try {
            content = client.responseToString(client.doPost("https://api.twitter.com/oauth2/invalidate_token", params, headers));
            JSONObject bearer_json = (JSONObject)JSONValue.parse(content);
            String access_token = bearer_json.get("access_token").toString();

            if(access_token!=null && access_token.length()>0)
            {
               this.setBearerToken(null);
               this.serialize();
               return true;
            }
         } catch(Exception ex) {
         } finally {
            client.close();
         }
      }
      return false;
   }

   public void setBearerToken(String bearertoken)
   {
      this.bearerToken = bearertoken;
   }

   public String getBearerToken()
   {
      return this.bearerToken;
   }

   @Override
   public String getSerializerFileName() {
      return "twittercredentials.xml";
   }

}
