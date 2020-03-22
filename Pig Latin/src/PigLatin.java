package piglatin;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Translates English words to Pig Latin.
 *
 * @author jaimin
 * @date November 8, 2019
 */
public class PigLatin {

    //controls whether or not "-" is added before the suffix of a word.
    static boolean learningMode = false;

    /**
     * Translates a single word to pig Latin.
     *
     * @param word: word to be converted to pig Latin.
     * @return the word passed in, converted to pig Latin.
     */
    public static String translateWord(String word) {
        int o;
        String translated = "";
        String pigLatin = "";

        // Get the regex to be checked 
        String regex = "[a-zA-Z]";
        // Create a pattern from regex 
        Pattern pattern = Pattern.compile(regex);
        // Create a matcher for the input String 
        Matcher matcher = pattern.matcher(word);

        //checks if there are actually any letters in the word
        if (matcher.find()) {
            //checks if it starts with a vowel. (Y is a consonant in this case.)
            if (isVowelNoY(word, 0)) {
                translated = translated + (word + "ay");
            } else {

                /*checks for "qu" beginning and replaces it while checking for a vowel.
                so that the first vowel found won't end up as a "u" that's following a "q"*/
                word = word.replaceAll("qu", "Àà");
                o = vowelFinder(word);
                word = word.replaceAll("Àà", "qu");

                //converts the first letter to a lower case and adds the rest of the word.
                String lowercased = word.substring(0, 1).toLowerCase() + word.substring(1);

                //creates the string with the suffix. adds a "-" if learning mode is on.
                //checks if first letter of a word was originally capitalized
                //uses this to capitalize the word.
                if (learningMode) {
                    if (word.length() == vowelFinder(word)) {
                    pigLatin = word.substring(0, o) + "-" + "ay";
                    } else {
                    pigLatin = (word.substring(o)) + "-" + lowercased.substring(0, o) + "ay";    
                    }

                    if (Character.isUpperCase(word.charAt(0))) {
                        pigLatin = pigLatin.substring(0, 1).toUpperCase() + pigLatin.substring(1);
                    }
                } else {
                    pigLatin = (word.substring(o)) + lowercased.substring(0, o) + "ay";

                    if (Character.isUpperCase(word.charAt(0))) {
                        pigLatin = pigLatin.substring(0, 1).toUpperCase() + pigLatin.substring(1);
                    }
                }

                translated = translated + pigLatin;
            }

        } else {
            translated = translated + word;
        }

        return translated;
    }

    /**
     * checks for a vowel at a given location in the string.
     *
     * @param word: word to be checked for a vowel.
     * @param location: the character location that should be checked.
     * @return returns a boolean on whether or not a vowel was found at that
     * location.
     */
    private static boolean isVowel(String word, int location) {
        switch (word.charAt(location)) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
            case 'y':
            case 'A':
            case 'E':
            case 'I':
            case 'O':
            case 'U':
            case 'Y':
                return true;
            default:
                return false;
        }
    }

    /**
     * checks for a vowel at a given location in the string. (doesn't include Y)
     *
     * @param word: word to be checked for a vowel.
     * @param location: the character location that should be checked.
     * @return returns a boolean on whether or not a vowel was found at that
     * location.
     */
    private static boolean isVowelNoY(String word, int location) {
        switch (word.charAt(location)) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
            case 'A':
            case 'E':
            case 'I':
            case 'O':
            case 'U':
                return true;
            default:
                return false;
        }

    }

    /**
     * Checks all characters of a string for a vowel.
     *
     * @param word: the word that's checked for a vowel.
     * @return the character location where the first vowel is found
     */
    private static int vowelFinder(String word) {
        int o;
        for (o = 1; o < word.length(); o++) {

            if (isVowel(word, o) == true) {
                break;
            }
        }
        return o;
    }

    /**
     * Translates a sentence to pig Latin.
     *
     * @param line: sentence that needs to be translated.
     * @return the full sentence converted to pig Latin.
     */
    public static String translateSentence(String line) {
        String finalSentence = "";

        //checks for any apostrophes, and replaces them with a non-english set of characters
        line = line.replaceAll("'", "Öö");

        //divides all words by boundary characters.
        String words[] = line.split("\\b");

        //translates each word using the translateWord method.
        for (int i = 0; i < words.length; i++) {
            finalSentence = finalSentence + translateWord(words[i]);
        }
        finalSentence = finalSentence.replaceAll("Öö", "'");
        return finalSentence;
    }
}
