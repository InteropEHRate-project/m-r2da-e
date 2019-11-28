package eu.interopehrate.mr2de.ncp;

import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Maintains the list of the registered NCP instances.
 */
public final class NCPRegistry {

    private final static Map<String, NCPDescriptor> registry = new HashMap<String, NCPDescriptor>();

    private static final String NCP_TAG_NAME = "ncp";
    private static final String COUNTRY_ATTR_NAME = "country";
    private static final String SUPPORTS_FHIR_ATTR_NAME = "supportsFHIR";
    private static final String SUPPORTS_EHDSI_ATTR_NAME = "supportsEHDSI";

    /**
     * Loads NCP configuration file from XML file
     */
    public static void loadConfiguration(XmlResourceParser parser) throws IOException, XmlPullParserException {
        NCPDescriptor ncp = null;
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {

            if (event == XmlPullParser.START_TAG) {
                if (NCP_TAG_NAME.equals(parser.getName())) {
                    ncp = new NCPDescriptor();

                    ncp.setCountry(parser.getAttributeValue(null, COUNTRY_ATTR_NAME))
                            .setSupportsEHDSI(Boolean.parseBoolean(parser.getAttributeValue(null, SUPPORTS_EHDSI_ATTR_NAME)))
                            .setSupportsFHIR(Boolean.parseBoolean(parser.getAttributeValue(null, SUPPORTS_FHIR_ATTR_NAME)))
                            .setEndpoint(parser.nextText());

                    Log.d(NCPRegistry.class.getName(), "Registered NCP for country: " + ncp.getCountry());
                    registry.put(ncp.getCountry(), ncp);
                }
            }
            event = parser.next();
        }
    }

    /**
     *
     * @param iso3166Alpha3Country: three letters country code as specified in ISO 3166
     * @return the corresponding NCDDescriptor instance;
     */
    public static NCPDescriptor getNCPDescriptor(String iso3166Alpha3Country) {
        if (iso3166Alpha3Country == null || iso3166Alpha3Country.isEmpty())
            throw new IllegalArgumentException("MR2D precondition error: Argument 'country' cannot be null or empty.");

        Log.d(NCPRegistry.class.getName(), "Retrieving NCP descriptor for country: " + iso3166Alpha3Country);
        return registry.get(iso3166Alpha3Country);
    }

    /**
     *
     * @param
     * @return the list of NCP descriptors;
     */
    public static Collection<NCPDescriptor> getNCPDescriptors() {
        return NCPRegistry.registry.values();
    }
}
