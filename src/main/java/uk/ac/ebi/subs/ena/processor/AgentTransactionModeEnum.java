package uk.ac.ebi.subs.ena.processor;

/**
 * Created by neilg on 12/04/2017.
 */
public enum AgentTransactionModeEnum {
    COMMIT_EACH_SUBMITTABLE(true,true),
    COMMIT_FULL_SUBMISSION(false,true),
    NO_COMMIT(false,false);

    boolean commitSubmittable;
    boolean commitFullSubmission;

    AgentTransactionModeEnum(boolean commitSubmittable, boolean commitFullSubmission) {
        this.commitSubmittable = commitSubmittable;
        this.commitFullSubmission = commitFullSubmission;
    }

    public boolean isCommitSubmittable() {
        return commitSubmittable;
    }

    public void setCommitSubmittable(boolean commitSubmittable) {
        this.commitSubmittable = commitSubmittable;
    }

    public boolean isCommitFullSubmission() {
        return commitFullSubmission;
    }

    public void setCommitFullSubmission(boolean commitFullSubmission) {
        this.commitFullSubmission = commitFullSubmission;
    }
}
