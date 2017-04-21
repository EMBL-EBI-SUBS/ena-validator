package uk.ac.ebi.subs.ena.accession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SRAAccessionFactory {
    // Accessions generated using sequences.
    //
    ENA_SAMPLE(            "SAMPLE_SEQ",             AccessionGenerator.SEQUENCE, "ERS", "%s%06d"), // Zero padding with minimum 6 numbers.
    ENA_STUDY(             "STUDY_SEQ",              AccessionGenerator.SEQUENCE, "ERP", "%s%06d"), // Zero padding with minimum 6 numbers.
    ENA_SUBMISSION(        "SUBMISSION_SEQ",         AccessionGenerator.SEQUENCE, "ERA", "%s%06d"), // Zero padding with minimum 6 numbers.
    ENA_EXPERIMENT(        "EXPERIMENT_SEQ",         AccessionGenerator.SEQUENCE, "ERX", "%s%06d"), // Zero padding with minimum 6 numbers.
    ENA_RUN(               "RUN_SEQ",                AccessionGenerator.SEQUENCE, "ERR", "%s%06d"), // Zero padding with minimum 6 numbers.
    ENA_ANALYSIS(          "ANALYSIS_SEQ",           AccessionGenerator.SEQUENCE, "ERZ", "%s%06d"), // Zero padding with minimum 6 numbers.
    ENA_SUBMISSION_FILE(   "SUBMISSION_FILE_SEQ",    AccessionGenerator.SEQUENCE, "ERF", "%s%06d"), // Zero padding with minimum 6 numbers.
    ENA_SUBMISSION_ACCOUNT("SUBMISSION_ACCOUNT_SEQ", AccessionGenerator.SEQUENCE, "Webin-", "%s%d"), // No zero padding.

    // Accessions generated using prefix_pkg.
    //
    ENA_PROJECT(       "ENA_PROJECT",        AccessionGenerator.PACKAGE),
    ARRAYEXPRESS_STUDY("ARRAYEXPRESS_STUDY", AccessionGenerator.PACKAGE),
    EGA_SAMPLE(        "EGA_SAMPLE",         AccessionGenerator.PACKAGE),
    EGA_SAMPLE_GROUP(  "EGA_SAMPLE_GROUP",   AccessionGenerator.PACKAGE),
    EGA_RUN(           "EGA_RUN",            AccessionGenerator.PACKAGE),
    EGA_ANALYSIS(      "EGA_ANALYSIS",       AccessionGenerator.PACKAGE),
    EGA_EXPERIMENT(    "EGA_EXPERIMENT",     AccessionGenerator.PACKAGE),
    ENA_SAMPLE_GROUP(  "ENA_SAMPLE_GROUP",   AccessionGenerator.PACKAGE),
    EGA_STUDY(         "EGA_STUDY",          AccessionGenerator.PACKAGE),
    EGA_DATASET(       "EGA_DATASET",        AccessionGenerator.PACKAGE),
    EGA_POLICY(        "EGA_POLICY",         AccessionGenerator.PACKAGE),
    EGA_DAC(           "EGA_DAC",            AccessionGenerator.PACKAGE),
    EGA_SUBMISSION(    "EGA_SUBMISSION",     AccessionGenerator.PACKAGE);

    private static final String ACCESSION_FROM_SEQUENCE_SQL = "select era.%s.nextval from dual";
    private static final String ACCESSION_FROM_PACKAGE_SQL = "select prefix_pkg.get_acc(?) from dual";

    enum AccessionGenerator {
        SEQUENCE,
        PACKAGE
    }

    private String name;
    private AccessionGenerator accessionGenerator;
    private String accessionPrefix;
    private String accessionFormat;

    SRAAccessionFactory(String name, AccessionGenerator accessionGenerator) {
        this.name = name;
        this.accessionGenerator = accessionGenerator;
    }

    SRAAccessionFactory(String name, AccessionGenerator accessionGenerator, String accessionPrefix, String accessionFormat) {
        this.name = name;
        this.accessionGenerator = accessionGenerator;
        this.accessionPrefix = accessionPrefix;
        this.accessionFormat = accessionFormat;
    }

    private static final Logger log = LoggerFactory.getLogger(SRAAccessionFactory.class);


}
