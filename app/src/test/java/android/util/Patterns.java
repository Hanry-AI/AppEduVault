package android.util;

import java.util.regex.Pattern;

/**
 * Stub class for JVM unit tests to mock android.util.Patterns without Robolectric/Mockito.
 */
public class Patterns {
    public static final Pattern EMAIL_ADDRESS = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
        "\\@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
    );
}
