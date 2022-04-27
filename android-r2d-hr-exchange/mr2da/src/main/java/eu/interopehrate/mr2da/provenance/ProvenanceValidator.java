package eu.interopehrate.mr2da.provenance;

import android.util.Log;

import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uhn.fhir.parser.IParser;
import eu.interopehrate.m_rds_sm.CryptoManagementFactory;
import eu.interopehrate.m_rds_sm.api.CryptoManagement;
import eu.interopehrate.mr2da.fhir.ConnectionFactory;

public class ProvenanceValidator {
    private CryptoManagement cryptoManagement;
    private IParser parser;

    public ProvenanceValidator() {
        cryptoManagement = CryptoManagementFactory.create(
                "http://interoperate-ejbca-service.euprojects.net");

        parser = ConnectionFactory.getFHIRParser().setPrettyPrint(false);
    }

    public ProvenanceValidationResults validateBundle(Bundle theBundle) throws Exception {
        ProvenanceValidationResults res = new ProvenanceValidationResults();
        List<Provenance> provenances = getSignedProvenances(theBundle);

        if (provenances.size() == 0 && theBundle.getEntry().size() > 0) {
            res.setSuccessful(false);
            res.setErrorMsg("No Provenance information in the received bundle!");
            return res;
        }

        Resource resourceToValidate;
        String jwsToken;
        IIdType id;
        String resourceId;
        for (Provenance provenance : provenances) {
            id = provenance.getTargetFirstRep().getReferenceElement();
            resourceId = id.getResourceType() + "/" + id.getIdPart();
            Log.d("MR2DA", "Validating resource: " + resourceId
                    + " from Provenance: " + provenance.getId());

            resourceToValidate = getResourceById(theBundle, resourceId);

            if (resourceToValidate != null) {
                jwsToken = new String(provenance.getSignatureFirstRep().getData());
                if (cryptoManagement.verifyDetachedJws(jwsToken,
                        parser.encodeResourceToString(resourceToValidate))) {
                    Log.d("MR2DA", "resource is valid!");
                    res.addValidationResult(resourceToValidate.getId(), true);
                } else {
                    Log.d("MR2DA", "resource is NOT valid!");
                    res.addValidationResult(resourceToValidate.getId(), false);
                }
            } else {
                Log.e("MR2DA", "Resource " + resourceId + " NOT found in bundle!");
                res.addValidationResult(resourceId, false);
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

    /**
     * Looks for the resource identified by the provided id
     *
     * @param theBundle
     * @param id
     * @return
     */
    private Resource getResourceById(Bundle theBundle, String id) {
        IIdType currentIdType;
        String currentId;
        for (Bundle.BundleEntryComponent entry : theBundle.getEntry()) {
            currentIdType = entry.getResource().getIdElement();
            currentId = currentIdType.getResourceType() + "/" + currentIdType.getIdPart();
            // Log.d("MR2DA", "id: " + id + ", currentId: " + currentId);
            if (id.equals(currentId))
                return entry.getResource();
        }

        return null;
    }

}
