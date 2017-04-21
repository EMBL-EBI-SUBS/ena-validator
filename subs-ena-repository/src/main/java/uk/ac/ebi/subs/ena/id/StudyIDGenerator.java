package uk.ac.ebi.subs.ena.id;

/**
 * Created by neilg on 02/04/2017.
 */
public class StudyIDGenerator extends SRAIDGenerator {
    public static final String SEQUENCE_NAME = "STUDY_SEQ";
    public static final String PREFIX = "ERP";
    public static final String FORMAT = "%s%06d";

    public StudyIDGenerator() {
        super(SEQUENCE_NAME, PREFIX, FORMAT);
    }
}
