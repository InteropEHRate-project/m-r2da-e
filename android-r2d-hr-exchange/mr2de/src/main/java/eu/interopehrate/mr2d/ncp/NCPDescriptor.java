package eu.interopehrate.mr2d.ncp;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Class used to describe features of an NCP instance.
 */
public final class NCPDescriptor {
    private String country; //ISO 3166 alpha 3
    private String endpoint;
    private boolean supportsFHIR;
    private boolean supportsEHDSI;

    public String getCountry() {
        return country;
    }

    public NCPDescriptor setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public NCPDescriptor setEndpoint(String endpoint) {
        this.endpoint = endpoint;
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
                ", endpoint='" + endpoint + '\'' +
                ", supportsFHIR=" + supportsFHIR +
                ", supportsEHDSI=" + supportsEHDSI +
                '}';
    }
}
