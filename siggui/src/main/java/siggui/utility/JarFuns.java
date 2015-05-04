package siggui.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JarFuns {

    public static String getJarName(Class<?> cls) {
        File file = new File(cls.getProtectionDomain().getCodeSource().getLocation().getPath());
        return file.getName();
    }

    public static String getPomProperty(String groupId, String artifactId, String property, Class<?> cls) {
        String value = "";
        String resource = "/META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties";
        InputStream is = cls.getResourceAsStream(resource);
        if (is != null) {
            try {
                Properties p = new Properties();
                p.load(is);
                value = p.getProperty(property, "");
            } catch (Exception e) {
                Logger.error(JarFuns.class, e);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    private JarFuns() {
    }
}
