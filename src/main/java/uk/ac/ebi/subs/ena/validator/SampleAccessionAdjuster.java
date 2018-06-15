package uk.ac.ebi.subs.ena.validator;


import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.data.submittable.Assay;

import java.util.Collection;

/**
 * At submission, we will provide ENA with a sample accession from BioSamples
 * At validation, we have not yet generated an accession, so we provide a known good sample accession, providing a sample
 *  alias is provided.
 */
public class SampleAccessionAdjuster {

    public static void fixSampleAccession(Assay assay){
        Collection<SampleUse> sampleUses = assay.getSampleUses();
        if (sampleUses != null){
            sampleUses.stream()
                    .filter(sampleUse -> sampleUse.getSampleRef() != null)
                    .map(sampleUse -> sampleUse.getSampleRef())
                    .filter(sampleRef -> !sampleRef.isAccessioned())
                    .filter(sampleRef -> sampleRef.getAlias() != null)
                    .forEach(sampleRef -> sampleRef.setAccession("SAMEA4452027"));
        }
    }
}
