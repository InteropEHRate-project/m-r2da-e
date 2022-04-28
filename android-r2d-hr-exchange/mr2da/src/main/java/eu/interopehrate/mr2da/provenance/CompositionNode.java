package eu.interopehrate.mr2da.provenance;

import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Composition.SectionComponent;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public class CompositionNode extends ResourceNode {

	CompositionNode(Composition resource) {
		super(resource);
	}

	@Override
	void loadChildren(ResourceNode root) {
		Composition composition = (Composition)resource;
		
		Resource currentResource;
		for (SectionComponent currentSection : composition.getSection()) {
        	
        	for (Reference currentRef : currentSection.getEntry()) {
        		currentResource = (Resource) currentRef.getResource();
        		if (currentResource != null) {
        			
	    			ResourceNode n = root.searchNodeByResourceId(currentResource.getId());
	    			if (n == null) {
	    				n = NodeFactory.createNode(currentResource);
					    n.loadChildren(this);
	    			} 
					n.setParent(this);
        		}        		
        	}
        }
	}
	
}
