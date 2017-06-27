package uk.ac.ebi.subs.ena.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by neilg on 24/05/2017.
 */
@Service
public class ENAProcessorContainerImpl implements ENAProcessorContainerService {

    protected static final Logger logger = LoggerFactory.getLogger(ENAProcessorContainerService.class);

    Map<String,ENAAgentProcessor> enaAgentProcessorMap;

    List<ENAAgentProcessor> enaAgentProcessorList = new ArrayList<>();

    ENAProcessorContainerImpl(ENAStudyProcessor enaStudyProcessor, ENASampleProcessor enaSampleProcessor,
                              ENAExperimentProcessor enaExperimentProcessor, @Value("${ena.processor_list}") String [] enaProcessorNames) {
        enaAgentProcessorMap = new HashMap<>();
        enaAgentProcessorMap.put(enaStudyProcessor.getName(),enaStudyProcessor);
        enaAgentProcessorMap.put(enaSampleProcessor.getName(),enaSampleProcessor);
        enaAgentProcessorMap.put(enaExperimentProcessor.getName(),enaExperimentProcessor);
        ENAAgentProcessor [] enaAgentProcessors = {enaStudyProcessor,enaSampleProcessor,enaExperimentProcessor};

        if (enaProcessorNames.length == 0) {
            enaAgentProcessorList = new ArrayList<>(enaAgentProcessorMap.values());
        } else {
            for (String agentProcessorName : enaProcessorNames) {
                final ENAAgentProcessor enaAgentProcessor = enaAgentProcessorMap.get(agentProcessorName);
                if (enaAgentProcessor != null) {
                    enaAgentProcessorList.add(enaAgentProcessor);
                    logger.info("Adding processor for class " + agentProcessorName);
                } else {
                    throw new RuntimeException("Processor not found for class " + agentProcessorName);
                }
            }
        }
    }

    @Override
    public List<ENAAgentProcessor> getENAAgentProcessorList() {
        return enaAgentProcessorList;
    }
}
