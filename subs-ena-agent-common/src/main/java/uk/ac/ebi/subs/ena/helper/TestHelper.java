package uk.ac.ebi.subs.ena.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    public static RUNSETDocument getRunSetDocument(String alias, String experimentAlias, String centerName, String fileName, String fileType) {
        RUNSETDocument runsetDocument = RUNSETDocument.Factory.newInstance();
        final RunType runType = runsetDocument.addNewRUNSET().addNewRUN();
        runType.setAlias(alias);
        runType.setCenterName(centerName);
        runType.addNewEXPERIMENTREF().addNewIDENTIFIERS().addNewSUBMITTERID().setStringValue(experimentAlias);
        final RunType.DATABLOCK datablock = runType.addNewDATABLOCK();
        final RunType.DATABLOCK.FILES.FILE file = datablock.addNewFILES().addNewFILE();
        file.setChecksumMethod(RunType.DATABLOCK.FILES.FILE.ChecksumMethod.MD_5);
        file.setChecksum("12345678123456781234567812345678");
        file.setFilename(fileName);
        file.setFiletype(RunType.DATABLOCK.FILES.FILE.Filetype.Enum.forString(fileType));
        return runsetDocument;
    }

    public static ENAStudy getENAStudy(String alias, Team team) throws Exception {
        ENAStudy enaStudy = new ENAStudy();
        enaStudy.setId(UUID.randomUUID().toString());
        enaStudy.setAlias(alias);
        enaStudy.setTeam(team);
        enaStudy.setStudyType("Whole Genome Sequencing");
        enaStudy.setTitle("Study Title");
        enaStudy.setStudyAbstract("Study abstract");
        return enaStudy;
    }

    public static ENASample getENASample(String alias, Team team) throws Exception {
        ENASample enaSample = new ENASample();
        enaSample.setId(UUID.randomUUID().toString());
        enaSample.setAlias(alias);
        enaSample.setTeam(team);
        enaSample.setTaxonId(9606l);
        enaSample.setTitle("Sample Title");
        enaSample.setDescription("Sample Description");
        return enaSample;
    }

    public static ENAExperiment getENAExperiment(String alias, Team team) throws Exception {
        ENAExperiment enaExperiment = new ENAExperiment();
        enaExperiment.setAlias(alias);
        enaExperiment.setTeam(team);
        enaExperiment.setId(UUID.randomUUID().toString());
        StudyRef studyRef = new StudyRef();
        studyRef.setAlias(alias);
        studyRef.setTeam(team.getName());
        enaExperiment.setStudyRef(studyRef);
        SampleRef sampleRef = new SampleRef();
        sampleRef.setAlias(alias);
        sampleRef.setTeam(team.getName());
        enaExperiment.setSampleRef(sampleRef);
        enaExperiment.setIllumina("Illumina Genome Analyzer");
        enaExperiment.setDesignDescription("Design Desc");
        enaExperiment.setLibraryName("Library name");
        enaExperiment.setLibraryLayout("SINGLE");
        enaExperiment.serialiseLibraryLayout();
        enaExperiment.setLibrarySelection("RANDOM");
        enaExperiment.setLibrarySource("GENOMIC");
        enaExperiment.setLibraryStrategy("WGS");
        return enaExperiment;
    }

    public static Team getTeam (String centerName) {
        Team team = new Team();
        team.setName(centerName);
        return team;
    }

    public static Study getStudy (String alias, Team team, String studyAbstract, String studyType) {
        Study study = new Study();
        study.setId(UUID.randomUUID().toString());
        study.setAlias(alias);
        study.setTeam(team);
        study.setTitle("Study Title");
        Attribute studyAbstractAttibute = new Attribute();
        studyAbstractAttibute.setName("study_abstract");
        studyAbstractAttibute.setValue(studyAbstract);
        study.getAttributes().add(studyAbstractAttibute);
        Attribute studyTypeAttribute = new Attribute();
        studyTypeAttribute.setName(ENAStudy.STUDY_TYPE);
        studyTypeAttribute.setValue(studyType);
        study.getAttributes().add(studyTypeAttribute);
        return study;
    }

    public static Sample getSample(String alias, Team team) {
        Sample sample = new Sample();
        sample.setId(UUID.randomUUID().toString());
        sample.setAlias(alias);
        sample.setTeam(team);
        sample.setTaxonId(9606l);
        sample.setDescription("Sample description");
        sample.setTitle("Sample title");
        return sample;
    }

    public static Assay getAssay(String alias, Team team, String sampleAlias, String studyAlias) {
        Assay a = new Assay();
        a.setId(UUID.randomUUID().toString());
        a.setAlias(alias);
        a.setTeam(team);
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
        Attribute designDescriptionAttribute = new Attribute();
        designDescriptionAttribute.setName(ENAExperiment.DESIGN_DESCRIPTION);
        designDescriptionAttribute.setValue("Design Description");
        a.getAttributes().add(designDescriptionAttribute);
        Attribute libraryName = new Attribute();
        libraryName.setName(ENAExperiment.LIBRARY_NAME);
        libraryName.setValue("Example Library");
        a.getAttributes().add(libraryName);
        Attribute librarySelection = new Attribute();
        librarySelection.setName(ENAExperiment.LIBRARY_SELECTION);
        librarySelection.setValue("RANDOM");
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
        file.setName("Test.fastq.gz");
        file.setChecksumMethod("MD5");
        assayData.getFiles().add(file);
        return assayData;
    }
}
