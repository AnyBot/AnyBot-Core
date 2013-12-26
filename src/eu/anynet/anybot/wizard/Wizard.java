/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.anynet.anybot.wizard;

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
   
   public void startWizard()
   {
      int i = 1, j = this.questions.size();
      for(WizardQuestion q : this.questions)
      {
         String answer;
         do 
         {
            // Question
            System.out.println("("+i+" of "+j+") "+q.getQuestion()+":");
            System.out.print("> ");
            
            // Answer
            Scanner s = new Scanner(System.in);
            answer = s.nextLine();
            if(q.isTrim())
            {
               answer = answer.trim();
            }
            
            // Check
            if(!q.isOk(answer))
            {
               System.out.println("Answer not correct! Please try again.");
            }
         }
         while(!q.isOk(answer));
         
         q.setAnswer(answer);
         i++;
      }
   }
   
}
