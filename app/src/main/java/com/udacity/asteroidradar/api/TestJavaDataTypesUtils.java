package com.udacity.asteroidradar.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This is a utility class for data - analysis
 * should needed to using Java - lambda
 * as well as Java-8 Streams
 */
public class TestJavaDataTypesUtils {

    public Map<Character, Long> inputCharactersAnalysis(String argStr, char[] inputCharacters) {

        Map<Character, Long> results = new HashMap<>();

        for (char charIn : inputCharacters) {
            switch (charIn) {
                case 'i':
                    long count_i = argStr.chars().filter(ch -> ch == 'i').count();
                    results.put(charIn, count_i);
                    break;
                case 'o':
                    long count_o = argStr.chars().filter(ch -> ch == 'o').count();
                    results.put(charIn, count_o);
                    break;
            }
        }

        for (Character c : results.keySet()) {
            System.out.println(String.format(Locale.US, "character: %s's \t,%d", c.toString(), results.get(c)));
        }
        ArrayList<String> ascOrderArrayNoStream = new ArrayList<>(
                Arrays.asList("Hi", "Guys", "Joy", "Ent", "sci", "how are you", "how", "bikes", "cycling")
        );

        ascOrderArrayNoStream.sort(String::compareTo);
        System.out.println(String.format(Locale.US, "lambda-sorting approach: %s", ascOrderArrayNoStream.toString()));
        ArrayList<String> ascOrderArrayStreamed = new ArrayList<>(
                Arrays.asList("Hi", "Guys", "Joy", "Ent", "sci", "how are you", "how", "bikes", "cycling")
        );

        System.out.println("\nJava8-streams() approach: -----------------");
        ascOrderArrayStreamed.stream()
                .map(String::toLowerCase)
                .sorted().forEach(System.out::println);

        return results;
    }
}
