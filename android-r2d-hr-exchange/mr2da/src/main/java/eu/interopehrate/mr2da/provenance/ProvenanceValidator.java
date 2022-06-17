package eu.interopehrate.mr2da.provenance;

import android.util.Log;

import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Provenance;

import java.util.ArrayList;
import java.util.List;

import ca.uhn.fhir.parser.IParser;
import eu.interopehrate.m_rds_sm.CryptoManagementFactory;
import eu.interopehrate.m_rds_sm.api.CryptoManagement;
import eu.interopehrate.mr2da.fhir.ConnectionFactory;
import eu.interopehrate.protocols.provenance.NodeFactory;
import eu.interopehrate.protocols.provenance.ResourceNode;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: executes the validation of the received bundle.
 */
public class ProvenanceValidator {
    public static final String NOT_VALIDATED_MSG = "Resource does not match with Provenance.";
    public static final String NOT_VALIDABLE_MSG = "Resource does not have a related Provenance.";

    private CryptoManagement cryptoManagement;
    private IParser parser;

    public ProvenanceValidator() {
        cryptoManagement = CryptoManagementFactory.create(
                "http://interoperate-ejbca-service.euprojects.net");

        parser = ConnectionFactory.getFHIRParser().setPrettyPrint(false);
    }

    /**
     * Validates the provenance of the resources
     *
     * @param theBundle
     * @return
     * @throws Exception
     */
    public ProvenanceValidationResults validateBundle(Bundle theBundle) throws Exception {
        ProvenanceValidationResults res = new ProvenanceValidationResults();

        // Retrieves the signed provenances from the bundle
        List<Provenance> provenances = getSignedProvenances(theBundle);

        // Creates the resource tree. The first level of this tree contains the
        // resources that MUST have a Provenance
        ResourceNode root = NodeFactory.createNode(theBundle);
        root.loadChildren(root);

        // Starts checking the provenance for each resource
        DomainResource resourceToValidate;
        String jwsToken;
        IIdType id;
        String resourceId;
        boolean foundProvenance = false;
        for (ResourceNode child : root.getChildren()) {
            resourceToValidate = (DomainResource)child.getResource();
            resourceId = resourceToValidate.getResourceType() +
                    "/" + resourceToValidate.getIdElement().getIdPart();

            foundProvenance = false;
            for (Provenance provenance : provenances) {
                if (matches(provenance, resourceToValidate)) {
                    foundProvenance = true;
                    Log.d("MR2DA", "Validating resource: " + resourceId
                            + " from Provenance: " + provenance.getId());

                    jwsToken = new String(provenance.getSignatureFirstRep().getData());
                    if (cryptoManagement.verifyDetachedJws(jwsToken,
                            parser.encodeResourceToString(resourceToValidate))) {
                        // Log.d("MR2DA", "resource is valid!");
                        res.addValidationResult(resourceId, true, "");
                    } else {
                        Log.d("MR2DA", "resource is NOT valid!");
                        res.addValidationResult(resourceId, false, NOT_VALIDATED_MSG);
                    }

                    break;
                }
            }

            if (!foundProvenance) {
                Log.e("MR2DA", "Provenance for " + resourceId + " NOT found in bundle. Resource is not valid.");
                res.addValidationResult(resourceId, false, NOT_VALIDABLE_MSG);
            }
        }

        return res;
    }

    /**
     * Retrieves signed Provenance in the bundle. Each signed Provenance represent
     * an health data to be validated.
     *
     * @param theBundle
     * @return
     */
    private List<Provenance> getSignedProvenances(Bundle theBundle) {
        List<Provenance> provenances = new ArrayList<Provenance>();

        // Extracts the list of Provenance
        Provenance currentProvenance;
        for (Bundle.BundleEntryComponent entry : theBundle.getEntry()) {
            if (entry.getResource()instanceof Provenance) {
                currentProvenance = (Provenance) entry.getResource();
                if (currentProvenance.getSignatureFirstRep().getData() != null)
                    provenances.add((Provenance) entry.getResource());
            }
        }

        return provenances;
    }

    private boolean matches (Provenance provenance, DomainResource resource) {
        if (provenance.getTarget().size() == 0)
            return false;

        DomainResource target = (DomainResource) provenance.getTargetFirstRep().getResource();
        final String targetId = target.getResourceType() + "/" + target.getIdElement().getIdPart();
        final String resourceId = resource.getResourceType() + "/" + resource.getIdElement().getIdPart();
        return resourceId.equals(targetId);
    }

}
