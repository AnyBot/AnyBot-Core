/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.anynet.anybot.wizard;

import eu.anynet.java.util.Properties;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author sim
 */
public class Wizard 
{

   private final ArrayList<WizardQuestion> questions;
   
   public Wizard()
   {
      this.questions = new ArrayList<>();
   }
   
   public void addQuestion(WizardQuestion q)
   {
      this.questions.add(q);
   }
   
   public Properties startWizard()
   {
      Properties answers = new Properties();
      int i = 1, j = this.questions.size();
      
      for(WizardQuestion q : this.questions)
      {
         String answer;
         do 
         {
            // Question
            System.out.print("("+i+" of "+j+") "+q.getQuestion()+": ");
            
            // Answer
            Scanner s = new Scanner(System.in);
            answer = s.nextLine();
            if(q.isTrim())
            {
               answer = answer.trim();
            }
            
            if(answer.isEmpty() && q.getDefaultAnswer()!=null)
            {
               answer = q.getDefaultAnswer();
            }
            
            // Check
            if(!q.isOk(answer))
            {
               System.out.println("Answer not correct! Please try again.");
            }
         }
         while(!q.isOk(answer));
         
         answers.set(q.getKey(), answer);
         
         i++;
      }
      
      return answers;
   }
   
   
   
}
