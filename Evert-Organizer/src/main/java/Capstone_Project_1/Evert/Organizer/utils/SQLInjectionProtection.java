
package Capstone_Project_1.Evert.Organizer.utils;

public class SQLInjectionProtection {

    // Common SQL injection patterns
    private static final String[] SQL_INJECTION_PATTERNS = {
            "'", "\"", ";", "--", "/*", "*/",
            "xp_", "sp_", "exec ", "execute ",
            "union ", "select ", "insert ", "update ",
            "delete ", "drop ", "alter ", "create ",
            "1=1", "1=0", "or ", "and ", "null",
            "sleep(", "waitfor ", "delay ", "shutdown"
    };  // Check the input with these patterns

    public static boolean isSafe(String input) {
        if (input == null) return true;
        String lowerInput = input.toLowerCase();

        for (String pattern : SQL_INJECTION_PATTERNS) {
            if (lowerInput.contains(pattern)) {
                return false;
            }
        }
        return true;
    }

    public static String sanitize(String input) {
        if (input == null) return null;
        String sanitized = input;
        for (String pattern : SQL_INJECTION_PATTERNS) {
            sanitized = sanitized.replace(pattern, "");
        }
        return sanitized;
    }
}