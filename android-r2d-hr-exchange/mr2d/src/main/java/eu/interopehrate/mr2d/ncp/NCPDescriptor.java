package eu.interopehrate.mr2d.ncp;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Class used to describe features of an NCP instance.
 */

@Deprecated
public final class NCPDescriptor {
    private String country; //ISO 3166 alpha 3
    private boolean supportsFHIR;
    private boolean supportsEHDSI;
    private String fhirEndpoint;
    private String ehdsiEndpoint;
    private String iamEndpoint;

    public String getCountry() {
        return country;
    }

    public NCPDescriptor setCountry(String country) {
        this.country = country;
        return this;
    }
    public String getFhirEndpoint() {
        return fhirEndpoint;
    }

    public NCPDescriptor setFhirEndpoint(String fhirEndpoint) {
        this.fhirEndpoint = fhirEndpoint;
        return this;
    }

    public String getEhdsiEndpoint() {
        return ehdsiEndpoint;
    }

    public NCPDescriptor setEhdsiEndpoint(String ehdsiEndpoint) {
        this.ehdsiEndpoint = ehdsiEndpoint;
        return this;
    }

    public String getIamEndpoint() {
        return iamEndpoint;
    }

    public NCPDescriptor setIamEndpoint(String iamEndpoint) {
        this.iamEndpoint = iamEndpoint;
        return this;
    }

    public boolean isSupportsFHIR() {
        return supportsFHIR;
    }

    public NCPDescriptor setSupportsFHIR(boolean supportsFHIR) {
        this.supportsFHIR = supportsFHIR;
        return this;
    }

    public boolean isSupportsEHDSI() {
        return supportsEHDSI;
    }

    public NCPDescriptor setSupportsEHDSI(boolean supportsEHDSI) {
        this.supportsEHDSI = supportsEHDSI;
        return this;
    }

    @Override
    public String toString() {
        return "NCPDescriptor{" +
                "country='" + country + '\'' +
                ", supportsFHIR=" + supportsFHIR +
                ", supportsEHDSI=" + supportsEHDSI +
                ", fhirEndpoint='" + fhirEndpoint + '\'' +
                ", ehdsiEndpoint='" + ehdsiEndpoint + '\'' +
                ", iamEndpoint='" + iamEndpoint + '\'' +
                '}';
    }
}
