import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Properties;

public class MavenWrapperDownloader {

    private static final String WRAPPER_VERSION = "0.5.6";
    private static final String DEFAULT_DOWNLOAD_URL = "https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/"
            + WRAPPER_VERSION + "/maven-wrapper-" + WRAPPER_VERSION + ".jar";
    private static final String MAVEN_WRAPPER_PROPERTIES_PATH = ".mvn/wrapper/maven-wrapper.properties";
    private static final String MAVEN_WRAPPER_JAR_PATH = ".mvn/wrapper/maven-wrapper.jar";
    private static final String PROPERTY_NAME_WRAPPER_URL = "wrapperUrl";

    public static void main(String args[]) {
        System.out.println("- Downloader started");
        File baseDirectory = new File(args[0]);
        System.out.println("- Using base directory: " + baseDirectory.getAbsolutePath());

        try {
            String url = getWrapperUrl(baseDirectory);
            System.out.println("- Downloading from: " + url);

            File outputFile = new File(baseDirectory.getAbsolutePath(), MAVEN_WRAPPER_JAR_PATH);
            if (!outputFile.getParentFile().exists()) {
                if (!outputFile.getParentFile().mkdirs()) {
                    System.out.println("- ERROR creating output directory '"
                            + outputFile.getParentFile().getAbsolutePath() + "'");
                    return;
                }
            }
            System.out.println("- Downloading to: " + outputFile.getAbsolutePath());
            downloadFileFromURL(url, outputFile);
            System.out.println("- Done");
        } catch (Exception e) {
            System.err.println("- Error downloading: " + e.getMessage());
        }
    }

    private static String getWrapperUrl(File baseDirectory) {
        String url = DEFAULT_DOWNLOAD_URL;
        File mavenWrapperPropertyFile = new File(baseDirectory, MAVEN_WRAPPER_PROPERTIES_PATH);
        if (mavenWrapperPropertyFile.exists()) {
            try (FileInputStream mavenWrapperPropertyFileInputStream = new FileInputStream(mavenWrapperPropertyFile)) {
                Properties mavenWrapperProperties = new Properties();
                mavenWrapperProperties.load(mavenWrapperPropertyFileInputStream);
                url = mavenWrapperProperties.getProperty(PROPERTY_NAME_WRAPPER_URL, url);
            } catch (IOException e) {
                System.out.println("- ERROR loading '" + MAVEN_WRAPPER_PROPERTIES_PATH + "'");
            }
        }
        return url;
    }

    private static void downloadFileFromURL(String urlString, File destination) throws Exception {
        URI uri = URI.create(urlString);
        URL website = uri.toURL();

        try (ReadableByteChannel rbc = Channels.newChannel(website.openStream()); FileOutputStream fos = new FileOutputStream(destination)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }
}
