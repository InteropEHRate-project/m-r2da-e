package eu.interopehrate.mr2de.r2d.executor;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import java.util.Hashtable;
import java.util.Map;

import eu.interopehrate.mr2de.api.HealthRecordBundle;
import eu.interopehrate.mr2de.api.HealthRecordType;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Implementation of HealthRecordBundle interface. DefaultHealthRecordBundle allows
 *  clients to iterate over query result in a NON-lazy way. Health data must have been completely
 *  downloaded and the provided to DefaultHealthRecordBundle.
 *
 */
public class DefaultHealthRecordBundle implements HealthRecordBundle {

    private static final String INDEX = "index";
    // Bundle of Bundles
    private Map<HealthRecordType, Bundle> bundlesMap = new Hashtable<>();
    private HealthRecordType[] types;

    public DefaultHealthRecordBundle(Bundle[] bundles, HealthRecordType[] types) {
        for (Bundle bundle: bundles) {
            if (bundle.getUserData(HealthRecordType.class.getName()) != null) {
                bundle.setUserData(INDEX, 0);
                bundlesMap.put((HealthRecordType)bundle.getUserData(HealthRecordType.class.getName()), bundle);
            }
        }
        this.types = types;
    }

    public DefaultHealthRecordBundle(Bundle bundle, HealthRecordType type) {
        this(new Bundle[] {bundle}, new HealthRecordType[] {type});
    }

    @Override
    public HealthRecordType[] getHealthRecordTypes() {
        return types;
    }

    @Override
    public boolean hasNext(HealthRecordType type) {
        Bundle bundle = bundlesMap.get(type);
        if (bundle == null)
            return false;
        else {
            int index = bundle.getUserInt(INDEX);
            return (index < bundle.getTotal());
        }
    }

    @Override
    public Resource next(HealthRecordType type) {
        Bundle bundle = bundlesMap.get(type);
        if (bundle == null)
            return null;
        else {
            int index = bundle.getUserInt(INDEX);
            Resource r = bundle.getEntry().get(index).getResource();
            bundle.setUserData(INDEX, ++index);
            return r;
        }
    }

    @Override
    public int getTotal(HealthRecordType type) {
        Bundle bundle = bundlesMap.get(type);
        if (bundle == null)
            return 0;
        else
            return bundle.getTotal();
    }

}
