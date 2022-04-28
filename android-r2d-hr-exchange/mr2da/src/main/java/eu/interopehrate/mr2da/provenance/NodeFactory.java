package eu.interopehrate.mr2da.provenance;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Resource;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public class NodeFactory {
	
	public static ResourceNode createNode(Resource resource) {
		if (resource instanceof DiagnosticReport)
			return new DiagnosticReportNode((DiagnosticReport)resource);
		else if (resource instanceof Composition)
			return new CompositionNode((Composition)resource);
		else if (resource instanceof Bundle)
			return new BundleNode((Bundle)resource);
		else 
			return new ResourceNode(resource);
	}

}
