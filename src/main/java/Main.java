import java.io.File;

public class Main {
    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.printf("Usage: java -jar %s {hypertree} {hypergraph} {query} {schema}", getJarName());
        }

        String gmlFileName = args[0];
        String dtlFileName = args[1];
        String sqlFileName = args[2];
        String schemaFileName = args[3];


    }

    public static String getJarName() {
        return new File(Main.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();
    }
}
