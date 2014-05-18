/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.anynet.anybot.wizard;

/**
 *
 * @author perry
 */
public class WizardQuestionFlag extends WizardQuestion 
{

   public WizardQuestionFlag(String key, String question) {
      super(key, question);
      this.checkregex = "^yes|no$";
      this.defaultanswer = "no";
   }
   
   @Override
   public String getQuestion() {
      String temp = super.getQuestion();
      return temp+" (yes or no)";
   }
   
}
