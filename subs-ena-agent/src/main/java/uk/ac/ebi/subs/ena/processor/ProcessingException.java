package uk.ac.ebi.subs.ena.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ProcessingException extends UncheckedIOException {
    private static final Logger logger = LoggerFactory.getLogger(ProcessingException.class);

    public ProcessingException(String message, IOException cause) {
        super(message, cause);
        logger.error("Processing exception " + message,cause);
    }

    public ProcessingException(IOException cause) {
        super(cause);
        logger.error("Processing exception",cause);
    }
}
