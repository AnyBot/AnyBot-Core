/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.anynet.anybot.wizard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author sim
 */
public class WizardQuestion 
{
   
   public static final String REGEX_ANY = "^.+$";
   public static final String REGEX_ANYOREMPTY = ".*";
   public static final String REGEX_IRCNICK = "^[a-zA-Z][a-zA-Z0-9.-_\\|]{0,32}$";
   public static final String REGEX_INTEGER = "^[0-9]+$";
   public static final String REGEX_ALPHANUMERIC = "^[0-9A-Za-z]+$";
   public static final String REGEX_ALPHANUMERIC_SPACES = "^[0-9A-Za-z ]+$";

   protected String key;
   protected String question;
   protected String checkregex;
   protected boolean caseinsensitive;
   protected boolean trim;
   
   protected String defaultanswer;
   
   public WizardQuestion(String key, String question)
   {
      this.key = key;
      this.question = question;
      this.checkregex = ".*";
      this.caseinsensitive = false;
      this.trim = true;
      this.defaultanswer = null;
   }
   
   
   
   public WizardQuestion setCaseInsensitive()
   {
      this.caseinsensitive = true;
      return this;
   }
   
   public WizardQuestion setCheck(String checkregex)
   {
      this.checkregex = checkregex;
      return this;
   }
   
   public WizardQuestion setDefault(String defaultanswer)
   {
      this.defaultanswer = defaultanswer;
      return this;
   }
   
   
   
   public boolean isOk(String str)
   {
      int patternconfig = (this.caseinsensitive ? Pattern.CASE_INSENSITIVE : 0);
		Pattern pattern = Pattern.compile(this.checkregex, patternconfig);
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
   }

   public String getQuestion() {
      String temp = this.question;
      if(this.defaultanswer!=null)
      {
         temp = temp+" ["+this.defaultanswer+"]";
      }
      return temp;
   }

   public String getCheckregex() {
      return checkregex;
   }

   public boolean isCaseinsensitive() {
      return caseinsensitive;
   }

   public boolean isTrim() {
      return trim;
   }
   
   public String getKey()
   {
      return this.key;
   }
   
   public String getDefaultAnswer()
   {
      return this.defaultanswer;
   }
   
   
}
