package uk.ac.ebi.subs.ena.repository;

import uk.ac.ebi.subs.ena.data.SubmissionStatus;
import uk.ac.ebi.subs.ena.data.SRAInfo;

/**
 * Created by neilg on 26/04/2017.
 */
public interface SubmittableTestObjectFactory<E extends SRAInfo> {
    String getId();

    String getAlias();

    default String getSubmissionId() {
        return "ERA000001";
    }

    default String getSubmissionAccountId () {
        return "Webin-2";
    }

    E createSubmittable (String alias, String submissionAccountId, SubmissionStatus submissionStatus);
}
