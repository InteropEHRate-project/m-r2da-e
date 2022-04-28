package eu.interopehrate.mr2da.provenance;

import java.util.List;

import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportMediaComponent;
import org.hl7.fhir.r4.model.Media;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public class DiagnosticReportNode extends ResourceNode {

	DiagnosticReportNode (DiagnosticReport resource) {
		super(resource);
		this.resource = resource;
	}

	@Override
	void loadChildren(ResourceNode root) {
		List<Reference> resRefs = ((DiagnosticReport)resource).getResult();
		Resource currentResource;
        for (Reference ref : resRefs) {
        	currentResource = (Resource)ref.getResource();
    		if (currentResource != null) {
    			ResourceNode n = root.searchNodeByResourceId(currentResource.getId());
    			if (n == null) {
    				n = NodeFactory.createNode(currentResource);
				    n.loadChildren(this);
    			} 
			    n.setParent(this);
    		}
        }
        
        List<DiagnosticReportMediaComponent> mediaList = ((DiagnosticReport)resource).getMedia();
        Media media;
        for (DiagnosticReportMediaComponent mediaComp : mediaList) {
        	media = (Media) mediaComp.getLink().getResource();
        	if (media != null) {
    			ResourceNode n = root.searchNodeByResourceId(media.getId());
    			if (n == null) {
    				n = NodeFactory.createNode(media);
				    n.loadChildren(this);
    			} 
				n.setParent(this);
        	}
        }
	}
	
}
