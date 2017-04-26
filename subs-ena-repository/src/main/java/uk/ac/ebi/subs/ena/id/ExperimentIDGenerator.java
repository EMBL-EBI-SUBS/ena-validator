package uk.ac.ebi.subs.ena.id;

/**
 * Created by neilg on 02/04/2017.
 */
public class ExperimentIDGenerator extends SRAIDGenerator {
    public static final String SEQUENCE_NAME = "SAMPLE_SEQ";
    public static final String PREFIX = "ERX";
    public static final String FORMAT = "%s%06d";

    public ExperimentIDGenerator() {
        super(SEQUENCE_NAME, PREFIX, FORMAT);
    }
}
