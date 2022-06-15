import hypergraph.Hypergraph;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.JSqlParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws JSQLParserException {
        System.out.println(CCJSqlParserUtil.parse("SELECT MIN(release.id) FROM \"work_attribute\", \"work_attribute_type_allowed_value\", \"work\", \"work_language\", \"iswc\", \"work_attribute_type\", \"language\", \"work_alias\", \"work_type\", \"release\", \"release_unknown_country\", \"release_group\", \"artist_credit\", \"release_packaging\", \"release_label\", \"release_status\", \"artist_credit_name\", \"release_group_secondary_type_join\", \"track\", \"release_group_secondary_type\", \"artist\", \"medium\", \"artist_ipi\", \"artist_isni\" WHERE \"work_attribute\".\"work_attribute_type_allowed_value\" = \"work_attribute_type_allowed_value\".\"id\" AND \"work_attribute\".\"work\" = \"work\".\"id\" AND \"work\".\"id\" = \"work_language\".\"work\" AND \"work\".\"id\" = \"iswc\".\"work\" AND \"work_attribute\".\"work_attribute_type\" = \"work_attribute_type\".\"id\" AND \"work_attribute_type_allowed_value\".\"work_attribute_type\" = \"work_attribute_type\".\"id\" AND \"work_language\".\"language\" = \"language\".\"id\" AND \"work\".\"id\" = \"work_alias\".\"work\" AND \"work\".\"type\" = \"work_type\".\"id\" AND \"language\".\"id\" = \"release\".\"language\" AND \"release\".\"id\" = \"release_unknown_country\".\"release\" AND \"release\".\"release_group\" = \"release_group\".\"id\" AND \"release\".\"artist_credit\" = \"artist_credit\".\"id\" AND \"release_group\".\"artist_credit\" = \"artist_credit\".\"id\" AND \"release\".\"packaging\" = \"release_packaging\".\"id\" AND \"release\".\"id\" = \"release_label\".\"release\" AND \"release\".\"status\" = \"release_status\".\"id\" AND \"artist_credit\".\"id\" = \"artist_credit_name\".\"artist_credit\" AND \"release_group\".\"id\" = \"release_group_secondary_type_join\".\"release_group\" AND \"artist_credit\".\"id\" = \"track\".\"artist_credit\" AND \"release_group_secondary_type_join\".\"secondary_type\" = \"release_group_secondary_type\".\"id\" AND \"artist_credit_name\".\"artist\" = \"artist\".\"id\" AND \"track\".\"medium\" = \"medium\".\"id\" AND \"release\".\"id\" = \"medium\".\"release\" AND \"artist\".\"id\" = \"artist_ipi\".\"artist\" AND \"artist\".\"id\" = \"artist_isni\".\"artist\";\n"));
        String dtlFileName = args[0];

        String hypergraphFile = null;
        try {
            hypergraphFile = Files.lines(Path.of(args[0])).collect(Collectors.joining());
            Hypergraph hg = Hypergraph.fromDTL(hypergraphFile);

            hg.toPDF(Paths.get("hypergraph.pdf"));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getJarName() {
        return new File(Main.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();
    }
}
