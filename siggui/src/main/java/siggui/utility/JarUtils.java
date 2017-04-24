package siggui.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class JarUtils {

	public static String getJarName(Class<?> cls) {
		File file = new File(cls.getProtectionDomain().getCodeSource().getLocation().getPath());
		return file.getName();
	}

	public static String getPomProperty(String groupId, String artifactId, String property, Class<?> cls) {
		String resource = "/META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties";
		InputStream is = cls.getResourceAsStream(resource);
		try {
			Properties p = new Properties();
			p.load(is);
			return p.getProperty(property, "");
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}

	public static String getManifestEntry(String entryName, Class<?> cls) {
		String className = cls.getSimpleName() + ".class";
		String classPath = cls.getResource(className).toString();

		String manifestPath = classPath.substring(0, classPath.lastIndexOf('!') + 1)
				+ "/META-INF/MANIFEST.MF";
		Manifest manifest;
		try {
			manifest = new Manifest(new URL(manifestPath).openStream());
			Attributes attribute = manifest.getMainAttributes();
			return attribute.getValue(entryName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private JarUtils() {
	}
}
