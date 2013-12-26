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

   private String question;
   private String checkregex;
   private boolean caseinsensitive;
   private boolean trim;
   
   private String answer;
   
   public WizardQuestion(String question, String checkregex, boolean caseinsensitive)
   {
      this.question = question;
      this.checkregex = checkregex;
      this.caseinsensitive = caseinsensitive;
      this.trim = true;
      this.answer = null;
   }
   
   public WizardQuestion(String question, String checkregex)
   {
      this(question, checkregex, true);
   }
   
   public WizardQuestion(String question)
   {
      this(question, WizardQuestion.REGEX_ANYOREMPTY);
   }
   
   public boolean isOk(String str)
   {
      int patternconfig = (this.caseinsensitive ? Pattern.CASE_INSENSITIVE : 0);
		Pattern pattern = Pattern.compile(this.checkregex, patternconfig);
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
   }

   public String getQuestion() {
      return question;
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

   public String getAnswer() {
      return answer;
   }

   public void setAnswer(String answer) {
      this.answer = answer;
   }
   
   public boolean isAnswered()
   {
      return (this.answer!=null);
   }
   
   
}
