package android.net;

/**
 * Stub class for JVM unit tests to mock android.net.Uri without Robolectric/Mockito.
 */
public class Uri {
    private final String uriString;

    private Uri(String uriString) {
        this.uriString = uriString;
    }

    public static Uri parse(String uriString) {
        if (uriString == null) {
            throw new NullPointerException("uriString is null");
        }
        return new Uri(uriString);
    }

    public static String encode(String s) {
        return s;
    }

    public static String decode(String s) {
        return s;
    }

    public String getQueryParameter(String key) {
        if (uriString == null || !uriString.contains("?")) {
            return null;
        }
        String query = uriString.substring(uriString.indexOf("?") + 1);
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String paramKey = pair.substring(0, idx);
                String paramVal = pair.substring(idx + 1);
                if (paramKey.equals(key)) {
                    return paramVal;
                }
            }
        }
        return null;
    }
}
