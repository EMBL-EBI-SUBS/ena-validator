package uk.ac.ebi.subs.data.submittable;

import org.springframework.test.annotation.TestAnnotationUtils;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Team;

import javax.xml.crypto.Data;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by neilg on 10/04/2017.
 */
public class TestStudyFactory {

    public static final String WHOLE_GENOME_SEQUENCING_STUDY_TYPE = "Whole Genome Sequencing";

    /**
     * Create a minimal study
     * @param alias
     * @param team
     * @param releaseDate
     * @return
     */
    public static Study createStudy (String alias, Team team, Date releaseDate) {
        Study study = new Study();
        study.setId(UUID.randomUUID().toString());
        study.setTeam(team);
        study.setAlias(alias);
        study.setDescription("Description");
        study.setTitle("Title");
        Attribute attribute = new Attribute();
        attribute.setName(ENAStudy.EXISTING_STUDY_TYPE);
        attribute.setValue(WHOLE_GENOME_SEQUENCING_STUDY_TYPE);
        study.getAttributes().add(attribute);
        Attribute studyAbstract = new Attribute();
        studyAbstract.setName(ENAStudy.STUDY_ABSTRACT);
        studyAbstract.setValue("study abstract");
        study.getAttributes().add(studyAbstract);
        study.setReleaseDate(releaseDate);
        return study;
    }

    public static Study createStudy(Team team, Date releaseDate ) {
        return createStudy(UUID.randomUUID().toString(),team,releaseDate);
    }

    public static Study createStudy(Team team ) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        Date date = new Date(cal.getTimeInMillis());
        return createStudy(UUID.randomUUID().toString(),team,date);
    }

}
