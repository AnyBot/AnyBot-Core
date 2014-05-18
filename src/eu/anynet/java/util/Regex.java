/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.java.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author sim
 */
public class Regex {


    public static ArrayList<ArrayList<String>> findAllByRegex(String regex, String search) {
        Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(search);
        ArrayList<ArrayList<String>> results = new ArrayList<>();

		while (matcher.find()) {
            ArrayList<String> row = new ArrayList<>();
            for(int i=1; i<=matcher.groupCount(); i++) {
                row.add(matcher.group(i));
            }
            results.add(row);
		}

        return results;
    }

    public static String findByRegex(String regex, String search, int match, int group) {
        Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(search);
        String result = null;

        if(matcher.find()) {
            do {
                match--;
                if(group>0) {
                    result = matcher.group(group);
                } else {
                    result = matcher.group(matcher.groupCount());
                }
                //System.out.println(match+" --> "+result);
            } while(match!=0 && matcher.find());
        }

        return result;
    }

    public static String findByRegexFirst(String regex, String search) {
        return findByRegex(regex, search, 1, 1);
    }

    public static String findByRegexLast(String regex, String search) {
        return findByRegex(regex, search, -1, 1);
    }

    	/**
	 * Prueft ob eine Regex auf einen String wahr ist
	 * @param text
	 * @param regex
	 * @return boolean
	 */
	public static boolean isRegexTrue(String text, String regex) {
		return isRegexTrue(text, regex, false);
	}

	/**
	 * Prueft ob eine Regex auf einen String wahr ist
	 * @param text
	 * @param regex
	 * @param dotall
	 * @return boolean
	 */
	public static boolean isRegexTrue(String text, String regex, boolean dotall) {
		int patternconfig = (dotall ? Pattern.CASE_INSENSITIVE+Pattern.DOTALL : Pattern.CASE_INSENSITIVE);
		Pattern pattern = Pattern.compile(regex, patternconfig);
		Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}

    public static String addLeadingZeros(int i, int length) {
        String integer = Integer.toString(i);
        length = length-integer.length();

        if(length>0) {
            while(length>0) {
                integer="0"+integer;
                length--;
            }
        }

        return integer;
    }


    public static File[] searchFiles(File dir, final String filterRegex) throws FileNotFoundException {

        //--> Dateien aus dem Ordner filtern
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(isRegexTrue(pathname.getName(), filterRegex)) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        //--> Wenn der Ordner existiert
        if(!(dir.exists() && dir.isDirectory() && dir.canRead())) {
            throw new FileNotFoundException("Folder '"+dir+"' does not exists");
        }

        return dir.listFiles(fileFilter);
   }


   public static String quote(String str)
   {
      return Pattern.quote(str);
   }
   
   
   public static String replace(String rgx, String str, String replacement)
   {
      Pattern pattern = Pattern.compile(rgx);
      return pattern.matcher(str).replaceAll(replacement);
   }


}
