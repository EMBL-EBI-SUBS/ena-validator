package uk.ac.ebi.subs.ena.id;

/**
 * Created by neilg on 02/04/2017.
 */
public class RunIDGenerator extends SRAIDGenerator {
    public static final String SEQUENCE_NAME = "RUN_SEQ";
    public static final String PREFIX = "ERR";
    public static final String FORMAT = "%s%06d";

    public RunIDGenerator() {
        super(SEQUENCE_NAME, PREFIX, FORMAT);
    }
}
