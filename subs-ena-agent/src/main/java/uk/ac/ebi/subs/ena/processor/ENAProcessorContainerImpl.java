package uk.ac.ebi.subs.ena.processor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by neilg on 24/05/2017.
 */
@Service
public class ENAProcessorContainerImpl implements ENAProcessorContainerService{

    List<ENAAgentProcessor> enaAgentProcessorList = new ArrayList<>();

    ENAProcessorContainerImpl(ENAStudyProcessor enaStudyProcessor, ENASampleProcessor enaSampleProcessor,
                              ENAExperimentProcessor enaExperimentProcessor) {
        ENAAgentProcessor [] enaAgentProcessors = {enaStudyProcessor,enaSampleProcessor,enaExperimentProcessor};
        enaAgentProcessorList = Arrays.asList(enaAgentProcessors);
    }

    @Override
    public List<ENAAgentProcessor> getENAAgentProcessorList() {
        return enaAgentProcessorList;
    }
}
