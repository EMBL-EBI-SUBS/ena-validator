package uk.ac.ebi.subs.ena.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by neilg on 25/04/2017.
 */
public enum SubmissionStatus {
    DRAFT(1),
    PRIVATE(2),
    CANCELLED(3),
    PUBLIC(4),
    SUPRESSED(5),
    KILLED(6),
    TEMPORARY_SUPPRESSED(7),
    TEMPORARY_KILLED(8);

    private static class StatusIDMap {
        static Map<Integer,SubmissionStatus> map = new HashMap<>();
    }

    private int statusId;

    SubmissionStatus(int statusId) {
        this.statusId = statusId;
        StatusIDMap.map.put(statusId,this);
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public static SubmissionStatus getSubmissionStatus(int statusId) {
        return StatusIDMap.map.get(statusId);
    }

}
