package uk.ac.ebi.subs.ena.validator;

import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.helper.TestHelper;

import java.util.UUID;

/**
 * Created by karoly on 19/06/2017.
 */
public final class ValidatorTestUtil {

    public static Sample createSample() {
        String alias = getAlias();
        final Team team = getTeam("test-team");
        return TestHelper.getSample(alias, team);
    }

    public static Study createStudy(String centerName) {
        String alias = getAlias();
        final Team team = getTeam(centerName);
        return TestHelper.getStudy(alias, team);
    }

    public static Assay createAssay(String assayAlias, String centerName, String sampleAlias, String studyAlias) {
        String alias = getAlias();
        final Team team = getTeam(centerName);
        Assay assay = TestHelper.getAssay(assayAlias, team, sampleAlias, studyAlias);
        assay.setId(UUID.randomUUID().toString());
        return assay;
    }

    public static AssayData createAssayData(String assayDataAlias, String centerName, String assayAlias) {
        final Team team = getTeam(centerName);
        AssayData assayData = TestHelper.getAssayData(assayDataAlias, team, assayAlias);
        assayData.setId(UUID.randomUUID().toString());
        return assayData;
    }

    public static Team getTeam(String centerName) {
        return TestHelper.getTeam(centerName);
    }

    public static String getAlias() {
        return UUID.randomUUID().toString();
    }

    public static String getAccession() { return UUID.randomUUID().toString(); }
}
