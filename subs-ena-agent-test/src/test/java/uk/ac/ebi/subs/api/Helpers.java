package uk.ac.ebi.subs.api;


import org.w3c.dom.Attr;
import uk.ac.ebi.subs.data.component.*;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.status.SubmissionStatusEnum;
import uk.ac.ebi.subs.data.submittable.ENAExperiment;
import uk.ac.ebi.subs.data.submittable.ENAStudy;
import uk.ac.ebi.subs.repository.model.ProcessingStatus;
import uk.ac.ebi.subs.repository.model.Sample;
import uk.ac.ebi.subs.repository.model.Submission;
import uk.ac.ebi.subs.repository.model.SubmissionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Helpers {

    public static Submission generateSubmission() {
        Submission s = new Submission();
        s.setTeam(generateTestTeam());
        s.setSubmitter(generateTestSubmitter());

        return s;
    }

    private static Submitter generateTestSubmitter() {
        Submitter u = new Submitter();
        u.setEmail("test@test.org");
        return u;
    }

    public static List<Sample> generateTestSamples() {
        return generateTestSamples(2);
    }

    public static List<uk.ac.ebi.subs.data.client.Sample> generateTestClientSamples(int numberOfSamplesRequired) {
        List<uk.ac.ebi.subs.data.client.Sample> samples = new ArrayList<>(numberOfSamplesRequired);

        for (int i = 1; i <= numberOfSamplesRequired; i++) {
            uk.ac.ebi.subs.data.client.Sample s = new uk.ac.ebi.subs.data.client.Sample();
            samples.add(s);

            s.setAlias("D" + i);
            s.setTitle("Donor " + i);
            s.setDescription("Human sample donor");
            s.setTaxon("Homo sapiens");
            s.setTaxonId(9606L);

        }

        return samples;
    }

    public static List<uk.ac.ebi.subs.data.client.Study> generateTestENAClientStudies(int numberOfSamplesRequired) {
        List<uk.ac.ebi.subs.data.client.Study> studies = new ArrayList<>(numberOfSamplesRequired);

        for (int i = 1; i <= numberOfSamplesRequired; i++) {
            uk.ac.ebi.subs.data.client.Study s = new uk.ac.ebi.subs.data.client.Study();
            studies.add(s);
            s.setAlias("Study" + i);
            s.setTitle("Study Title " + i);
            s.setArchive(Archive.Ena);
            s.setDescription("Test study");
            Attribute attribute = new Attribute();
            attribute.setName(ENAStudy.EXISTING_STUDY_TYPE);
            attribute.setValue("Whole Genome Sequencing");
            s.getAttributes().add(attribute);
        }

        return studies;
    }

    public static List<uk.ac.ebi.subs.data.client.Assay> generateTestENAClientAssay(int numberOfAssaysRequired, String studyAlias, String sampleAlias) {
        List<uk.ac.ebi.subs.data.client.Assay> assayList = new ArrayList<>(numberOfAssaysRequired);

        for (int i = 1; i <= numberOfAssaysRequired; i++) {
            uk.ac.ebi.subs.data.client.Assay a = new uk.ac.ebi.subs.data.client.Assay();
            assayList.add(a);
            a.setAlias("Assay" + i);
            a.setTitle("Assay Title " + i);
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

        }

        return assayList;
    }

    public static List<uk.ac.ebi.subs.data.client.AssayData> generateTestENAClientAssayData(int numberOfAssayDatasRequired, String assayRefAlias) {
        List<uk.ac.ebi.subs.data.client.AssayData> assayDataList = new ArrayList<>(numberOfAssayDatasRequired);

        for (int i = 1; i <= numberOfAssayDatasRequired; i++) {
            uk.ac.ebi.subs.data.client.AssayData assayData = new uk.ac.ebi.subs.data.client.AssayData();
            assayData.setAlias("AssayData" + i);
            assayData.setTitle("Assay Data Title " + i);
            assayData.setArchive(Archive.Ena);
            assayData.setDescription("Test assay data");
            AssayRef assayRef = new AssayRef();
            assayRef.setAlias(assayRefAlias);
            assayData.setAssayRef(assayRef);
            assayDataList.add(assayData);
        }

        return assayDataList;
    }


    public static List<Sample> generateTestSamples(int numberOfSamplesRequired) {
        List<Sample> samples = new ArrayList<>(numberOfSamplesRequired);

        for (int i = 1; i <= numberOfSamplesRequired; i++) {
            Sample s = new Sample();
            samples.add(s);

            s.setId(createId());

            s.setAlias("D" + i);
            s.setTitle("Donor " + i);
            s.setDescription("Human sample donor");
            s.setTaxon("Homo sapiens");
            s.setTaxonId(9606L);

            s.setProcessingStatus(new ProcessingStatus(ProcessingStatusEnum.Draft));

        }

        return samples;
    }

    public static Team generateTestTeam() {
        Team d = new Team();
        d.setName("my-team");
        return d;
    }


    public static Submission generateTestSubmission() {
        Submission sub = new Submission();
        Team d = new Team();
        sub.setId(createId());

        sub.setTeam(generateTestTeam());

        sub.setSubmissionStatus(new SubmissionStatus(SubmissionStatusEnum.Draft));

        return sub;
    }

    private static String createId() {
        return UUID.randomUUID().toString();
    }
}
