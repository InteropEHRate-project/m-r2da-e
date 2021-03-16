package eu.interopehrate.mr2de.api;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;

import java.util.Date;

import eu.interopehrate.mr2d.exceptions.MR2DException;

@Deprecated
public interface MHD {

    /**
     * params:  patient, patient.identifier,
     *          created, author.given, author.family,
     *          identifier, type, source, status
     * @return
     * @throws MR2DException
     */
    Bundle searchDocumentManifest(CodeableConcept type,
                                  Date created) throws MR2DException;

    /**
     * params:  patient, patient.identifier,
     *          status, identifier, date, author.given, author.family,
     *          category, type, setting, period, facility, event,
     *          security-label, format, related
     *
     * @return
     * @throws MR2DException
     */
    Bundle searchDocumentReference(CodeableConcept category,
                                   CodeableConcept type,
                                   Date from, Date to) throws MR2DException;

}
