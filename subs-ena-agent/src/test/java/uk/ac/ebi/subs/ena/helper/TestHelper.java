package uk.ac.ebi.subs.ena.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.assertj.core.util.VisibleForTesting;
import org.junit.Test;
import uk.ac.ebi.ena.sra.xml.*;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.*;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.ProcessingCertificateEnvelope;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by neilg on 18/05/2017.
 */
public class TestHelper {



    public static STUDYSETDocument getStudysetDocument(String alias, String centerName) {
        STUDYSETDocument studysetDocument = STUDYSETDocument.Factory.newInstance();
        final StudyType studyType = studysetDocument.addNewSTUDYSET().addNewSTUDY();
        studyType.setAlias(alias);
        studyType.setCenterName(centerName);
        final StudyType.DESCRIPTOR descriptor = studyType.addNewDESCRIPTOR();
        descriptor.setSTUDYABSTRACT("Study Abstract");
        descriptor.setSTUDYTITLE("Study Title");
        descriptor.setSTUDYDESCRIPTION("Study Description");
        descriptor.addNewSTUDYTYPE().setExistingStudyType(StudyType.DESCRIPTOR.STUDYTYPE.ExistingStudyType.WHOLE_GENOME_SEQUENCING);
        return studysetDocument;
    }

    public static SAMPLESETDocument getSamplesetDocument(String alias, String centerName) {
        SAMPLESETDocument samplesetDocument = SAMPLESETDocument.Factory.newInstance();
        final SampleType sampleType = samplesetDocument.addNewSAMPLESET().addNewSAMPLE();
        sampleType.setAlias(alias);
        sampleType.setCenterName(centerName);
        final SampleType.SAMPLENAME samplename = sampleType.addNewSAMPLENAME();
        samplename.setTAXONID(9606);
        return samplesetDocument;
    }

    public static EXPERIMENTSETDocument getExperimentSetDocument(String alias, String studyAlias, String sampleAlias, String centerName) {
        EXPERIMENTSETDocument experimentsetDocument = EXPERIMENTSETDocument.Factory.newInstance();
        final ExperimentType experimentType = experimentsetDocument.addNewEXPERIMENTSET().addNewEXPERIMENT();
        experimentType.setAlias(alias);
        experimentType.setCenterName(centerName);
        experimentType.addNewSTUDYREF().setRefname(studyAlias);
        experimentType.addNewPLATFORM().addNewILLUMINA().setINSTRUMENTMODEL(TypeIlluminaModel.ILLUMINA_GENOME_ANALYZER);
        final LibraryType libraryType = experimentType.addNewDESIGN();
        libraryType.setDESIGNDESCRIPTION("design description");
        final LibraryDescriptorType libraryDescriptorType = libraryType.addNewLIBRARYDESCRIPTOR();
        libraryDescriptorType.addNewLIBRARYLAYOUT().addNewSINGLE();
        libraryDescriptorType.setLIBRARYSELECTION(TypeLibrarySelection.RANDOM);
        libraryDescriptorType.setLIBRARYNAME("Library Name");
        libraryDescriptorType.setLIBRARYSOURCE(TypeLibrarySource.GENOMIC);
        libraryDescriptorType.setLIBRARYSTRATEGY(TypeLibraryStrategy.WGS);
        final SampleDescriptorType sampleDescriptorType = libraryType.addNewSAMPLEDESCRIPTOR();
        sampleDescriptorType.addNewIDENTIFIERS().addNewSUBMITTERID().setStringValue(sampleAlias);
        return experimentsetDocument;
    }

    public static ENAStudy getENAStudy(String alias, Team team) throws Exception {
        ENAStudy enaStudy = new ENAStudy();
        enaStudy.setAlias(alias);
        enaStudy.setTeam(team);
        enaStudy.setStudyType("Whole Genome Sequencing");
        enaStudy.setTitle("Study Title");
        enaStudy.setStudyAbstract("Study abstract");
        return enaStudy;
    }

    public static ENASample getENASample(String alias, Team team) throws Exception {
        ENASample enaSample = new ENASample();
        enaSample.setAlias(alias);
        enaSample.setTeam(team);
        enaSample.setTaxonId(9606l);
        enaSample.setTitle("Sample Title");
        enaSample.setDescription("Sample Description");
        return enaSample;
    }

    public static Team getTeam (String centerName) {
        Team team = new Team();
        team.setName(centerName);
        return team;
    }

    public static Study getStudy (String alias, Team team) {
        Study study = new Study();
        study.setAlias(alias);
        study.setTeam(team);
        study.setTitle("Study Title");
        Attribute studyAbstractAttibute = new Attribute();
        studyAbstractAttibute.setName(ENAStudy.STUDY_ABSTRACT);
        studyAbstractAttibute.setValue("Study abstract");
        study.getAttributes().add(studyAbstractAttibute);
        Attribute studyTypeAttribute = new Attribute();
        studyTypeAttribute.setName(ENAStudy.EXISTING_STUDY_TYPE);
        studyTypeAttribute.setValue("Whole Genome Sequencing");
        study.getAttributes().add(studyTypeAttribute);
        return study;
    }

    public static Sample getSample(String alias, Team team) {
        Sample sample = new Sample();
        sample.setAlias(alias);
        sample.setTeam(team);
        sample.setTaxonId(9606l);
        sample.setDescription("Sample description");
        sample.setTitle("Sample title");
        return sample;
    }

    public static Assay getAssay(String alias, Team team, String sampleAlias, String studyAlias) {
        Assay a = new Assay();
        a.setAlias(alias);
        a.setTitle("Assay Title ");
        a.setArchive(Archive.Ena);
        a.setDescription("Test assay");
        Attribute platformAttribute = new Attribute();
        platformAttribute.setName(ENAExperiment.PLATFORM_TYPE);
        platformAttribute.setValue("Illumina");
        a.getAttributes().add(platformAttribute);
        Attribute instrumentAttribute = new Attribute();
        instrumentAttribute.setName(ENAExperiment.INSTRUMENT_MODEL);
        instrumentAttribute.setValue("Illumina Genome Analyzer");
        a.getAttributes().add(instrumentAttribute);
        Attribute libraryLayoutAttribute = new Attribute();
        libraryLayoutAttribute.setName(ENAExperiment.LIBRARY_LAYOUT);
        libraryLayoutAttribute.setValue(ENAExperiment.SINGLE);
        a.getAttributes().add(libraryLayoutAttribute);
        Attribute libraryName = new Attribute();
        libraryName.setName(ENAExperiment.LIBRARY_NAME);
        libraryName.setValue("Example Library");
        a.getAttributes().add(libraryName);
        Attribute librarySelection = new Attribute();
        librarySelection.setName(ENAExperiment.LIBRARY_SELECTION);
        librarySelection.setValue("Random");
        a.getAttributes().add(librarySelection);
        Attribute librarySource = new Attribute();
        librarySource.setName(ENAExperiment.LIBRARY_SOURCE);
        librarySource.setValue("GENOMIC");
        a.getAttributes().add(librarySource);
        Attribute libraryStratagy = new Attribute();
        libraryStratagy.setName(ENAExperiment.LIBRARY_STRATEGY);
        libraryStratagy.setValue("WGS");
        a.getAttributes().add(libraryStratagy);

        SampleRef sampleRef = new SampleRef();
        sampleRef.setAlias(sampleAlias);
        SampleUse sampleUse = new SampleUse(sampleRef);
        a.getSampleUses().add(sampleUse);

        StudyRef studyRef = new StudyRef();
        studyRef.setAlias(studyAlias);
        a.setStudyRef(studyRef);
        return a;
    }

    public static AssayData getAssayData (String alias, Team team, String assayAlias) {
        AssayData assayData = new AssayData();
        assayData.setAlias(alias);
        assayData.setTeam(team);
        assayData.setAlias(alias);
        AssayRef assayRef = new AssayRef();
        assayRef.setAlias(assayAlias);
        assayData.setAssayRef(assayRef);
        assayData.setTitle("Test Title");
        File file = new File();
        file.setType("fastq");
        file.setChecksum("12345678abcdefgh12345678abcdefgh");
        file.setName("Test.fastq");
        file.setChecksumMethod("MD5");
        assayData.getFiles().add(file);
        return assayData;
    }

    public static void main(String[] args) throws IOException, ParseException {
        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();
        String releaseDateString = "01-07-2017";
        final Date releaseDate = new SimpleDateFormat("dd-MM-yyyy").parse(releaseDateString);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        PrintWriter printWriter = new PrintWriter(System.out);

        String alias = UUID.randomUUID().toString();
        String submissionId = UUID.randomUUID().toString();
        final Team team = TestHelper.getTeam("test-team");
        final Study study = TestHelper.getStudy(alias, team);
        study.setArchive(Archive.Ena);
        study.setReleaseDate(releaseDate);
        study.setId(UUID.randomUUID().toString());
        ProcessingCertificate studyProcessingCertificate = new ProcessingCertificate(study,Archive.Ena, ProcessingStatusEnum.Received,"ERP023149");
        Submission submission = new Submission();
        submission.setTeam(team);
        submission.setId(submissionId);
        Date submissionDate = new Date();
        submission.setSubmissionDate(submissionDate);
        uk.ac.ebi.subs.data.component.Submitter submitter = new Submitter();
        submitter.setEmail("subs-dev@ebi.ac.uk");
        submission.setSubmitter(submitter);
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope(submission);
        final Sample sample = TestHelper.getSample(alias,team);
        sample.setArchive(Archive.BioSamples);
        sample.setId(UUID.randomUUID().toString());
        submissionEnvelope.getStudies().add(study);
        submissionEnvelope.getSamples().add(sample);
        ProcessingCertificate sampleProcessingCertificate = new ProcessingCertificate(sample,Archive.Ena, ProcessingStatusEnum.Received,"ERS129091");
        final Assay assay = TestHelper.getAssay(alias,team,alias,alias);
        assay.setId(UUID.randomUUID().toString());
        assay.setArchive(Archive.Ena);
        submissionEnvelope.getAssays().add(assay);
        ProcessingCertificate assayProcessingCertificate = new ProcessingCertificate(assay,Archive.Ena, ProcessingStatusEnum.Received,"ERX049598");
        final AssayData assayData = TestHelper.getAssayData(alias,team,alias);
        assayData.setId(UUID.randomUUID().toString());
        assayData.setArchive(Archive.Ena);
        submissionEnvelope.getAssayData().add(assayData);
        ProcessingCertificate assayDataProcessingCertificate = new ProcessingCertificate(assayData,Archive.Ena, ProcessingStatusEnum.Received,"ERR028450");
        objectMapper.writeValue(printWriter,submissionEnvelope);
        processingCertificateList.add(studyProcessingCertificate);
        processingCertificateList.add(sampleProcessingCertificate);
        processingCertificateList.add(assayProcessingCertificate);
        processingCertificateList.add(assayDataProcessingCertificate);
        ProcessingCertificateEnvelope processingCertificateEnvelope = new ProcessingCertificateEnvelope(submission.getId(),processingCertificateList);
        //objectMapper.writeValue(printWriter,processingCertificateEnvelope);
    }

}
