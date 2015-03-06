package org.protege.owl.diff.align.impl;

import java.util.Map;
import java.util.logging.Logger;

import org.protege.owl.diff.align.AlignmentAlgorithm;
import org.protege.owl.diff.align.AlignmentListener;
import org.protege.owl.diff.align.util.AlignmentListenerAdapter;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

public class OwlDiffMapImpl extends OwlDiffMapCore {
    private Logger logger = Logger.getLogger(OwlDiffMapImpl.class.getName());
    
    private static final String SEPARATOR_STRING = " **************** ";
    
    private OWLDataFactory factory;
    private OWLOntology sourceOntology;
    private OWLOntology targetOntology;

    
    private int           matchedEntitiesSinceAnnounce = 0;
    private int           matchedAxiomsSinceAnnounce = 0;
    private AlignmentAlgorithm lastAnnouncedDiffAlgorithm;
    private long          lastAnnounceTime = -1;
    
    
    
    private AlignmentListener trackingListener = new AlignmentListenerAdapter() {

        public void addMatch(OWLEntity source, OWLEntity target) {
            matchedEntitiesSinceAnnounce++;
        }

        public void addMatchingEntities(Map<OWLEntity, OWLEntity> newMatches) {
            matchedEntitiesSinceAnnounce += newMatches.size();
        }

        public void addMatchedAxiom(OWLAxiom axiom) {
            matchedAxiomsSinceAnnounce++;
        }
        
    };
    
    public OwlDiffMapImpl(OWLDataFactory factory,
                          OWLOntology sourceOntology, 
                          OWLOntology targetOntology) {
        super(factory, sourceOntology, targetOntology);
        this.factory = factory;
        this.sourceOntology = sourceOntology;
        this.targetOntology = targetOntology;
        addDiffListener(trackingListener);
    }
    
    /*
     * Getters
     */
    public OWLDataFactory getOWLDataFactory() {
        return factory;
    }
    
    public OWLOntology getSourceOntology() {
        return sourceOntology;
    }

    public OWLOntology getTargetOntology() {
        return targetOntology;
    }



    
    /*
     * Tracking
     */
    
    public void announce(AlignmentAlgorithm da) {
        logger.info(SEPARATOR_STRING + da.getAlgorithmName() + SEPARATOR_STRING);
        
        lastAnnouncedDiffAlgorithm = da;
        lastAnnounceTime = System.currentTimeMillis();
        matchedAxiomsSinceAnnounce = 0;
        matchedEntitiesSinceAnnounce = 0;
    }
    
    public void summarize() {
        long took = System.currentTimeMillis() - lastAnnounceTime;
        
        logger.info("Algorithm " + lastAnnouncedDiffAlgorithm.getAlgorithmName() + " completed.");
        logger.info(lastAnnouncedDiffAlgorithm.getAlgorithmName() 
        					+ " step took "  + took + " ms.");
        if (matchedEntitiesSinceAnnounce == 0) {
            logger.info("No progress made.");
        }
        else {
            logger.info("" + matchedEntitiesSinceAnnounce + " owl entities matched up.");
            logger.info("" + matchedAxiomsSinceAnnounce + " axioms matched up.");
        }
        logger.info(SEPARATOR_STRING);
    }
    


}
