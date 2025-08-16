package com.team.updevic001.utility;

public class IDGenerator {

    public static String normalizeString(String input) {
        if (input == null) {
            return null;
        }

        // Remove all special characters except spaces and alphanumerics
        String cleaned = input.replaceAll("[^a-zA-Z0-9\\s]", "");

        // Replace spaces with hyphens
        String hyphenated = cleaned.trim().replaceAll("\\s+", "-");

        // Convert to lowercase
        String lowercased = hyphenated.toLowerCase();

        // Truncate to 128 characters
        return lowercased.length() > 128
                ? lowercased.substring(0, 128)
                : lowercased;
    }


}
