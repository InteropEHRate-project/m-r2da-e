package eu.interopehrate.mr2deapp;

import org.hl7.fhir.r4.hapi.validation.FhirInstanceValidator;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.InputStream;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.IValidatorModule;
import ca.uhn.fhir.validation.ValidationResult;
import eu.interopehrate.mr2da.MR2DAContext;
import eu.interopehrate.mr2da.fhir.ConnectionFactory;
import eu.interopehrate.mr2da.provenance.ProvenanceValidationResults;
import eu.interopehrate.mr2da.provenance.ProvenanceValidator;

@RunWith(JUnit4.class)
public class ValidationTest {

    @Test
    public void testProvenanceValidation() throws Exception {

        InputStream resourceConfigFile = MR2DAContext.getMR2DAContext().getResources()
                .openRawResource(eu.interopehrate.mr2da.R.raw.signedresources);

        Bundle bundle = (Bundle)ConnectionFactory.getFHIRParser().parseResource(resourceConfigFile);

        ProvenanceValidator val = new ProvenanceValidator();
        ProvenanceValidationResults valRes = val.validateBundle(bundle);

        Assert.assertTrue(valRes.isSuccessful());
    }

    @Test
    public void testValidation() {
        FhirContext ctx = FhirContext.forR4();

        // Ask the context for a validator
        FhirValidator validator = ctx.newValidator();

        // validator.setValidateAgainstStandardSchema(false);
        // validator.setValidateAgainstStandardSchematron(false);

        // Create a validation module and register it
        validator.registerValidatorModule(new FhirInstanceValidator());

        // Pass a resource instance as input to be validated
        Patient resource = new Patient();
        resource.addName().setFamily("Simpson").addGiven("Homer");
        ValidationResult result = validator.validateWithResult(resource);

        Assert.assertTrue(result.isSuccessful());
    }

}
