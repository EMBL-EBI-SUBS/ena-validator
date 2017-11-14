package uk.ac.ebi.subs.ena.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Service;
import uk.ac.ebi.ena.sra.xml.ID;
import uk.ac.ebi.ena.sra.xml.RECEIPTDocument;
import uk.ac.ebi.ena.sra.xml.SubmissionType;
import uk.ac.ebi.subs.data.submittable.ENAStudy;
import uk.ac.ebi.subs.data.submittable.ENASubmittable;

import java.sql.Date;
import java.time.ZoneId;
import java.util.Calendar;

/**
 * Created by neilg on 12/04/2017.
 */
@Service
public class StudySRALoader extends AbstractSRALoaderService<ENAStudy> {
    public static final String STUDY_SCHEMA = "study";

    @Override
    SubmissionType.ACTIONS createActions(SubmissionType submissionType, ENASubmittable enaSubmittable, String schema, boolean validateOnly ) {
        final SubmissionType.ACTIONS actions = super.createActions(submissionType, enaSubmittable, schema, validateOnly);
        final SubmissionType.ACTIONS.ACTION.HOLD hold = actions.addNewACTION().addNewHOLD();
        ENAStudy enaStudy = (ENAStudy) enaSubmittable;
        Calendar calendar = Calendar.getInstance();
        if (enaStudy.getBaseObject().getReleaseDate() != null) {
            calendar.setTime(
                    Date.from(enaStudy.getBaseObject().getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        hold.setHoldUntilDate(calendar);
        if (enaSubmittable.getAccession() != null) {
            hold.setTarget(enaSubmittable.getAccession());
        }
        return actions;
    }

    public String getSchema() {
        return STUDY_SCHEMA;
    }

    @Override
    @Autowired
    @Qualifier("study")
    public void setMarshaller(Marshaller marshaller) {
        super.setMarshaller(marshaller);
    }

    @Override
    ID[] getIDs(RECEIPTDocument.RECEIPT receipt) {
        return receipt.getSTUDYArray();
    }
}
