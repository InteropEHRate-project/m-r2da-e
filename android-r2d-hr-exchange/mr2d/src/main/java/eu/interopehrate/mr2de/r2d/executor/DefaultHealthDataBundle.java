package eu.interopehrate.mr2de.r2d.executor;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import java.util.Hashtable;
import java.util.Map;

import eu.interopehrate.mr2de.api.HealthDataBundle;
import eu.interopehrate.mr2de.api.HealthDataType;

/**
 *       Author: Engineering S.p.A. (www.eng.it)
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Implementation of HealthRecordBundle interface. DefaultHealthRecordBundle allows
 *  clients to iterate over query result in a NON-lazy way. Health data must have been completely
 *  downloaded and the provided to DefaultHealthRecordBundle.
 *
 */

@Deprecated
public class DefaultHealthDataBundle implements HealthDataBundle {

    private static final String INDEX = "index";
    // Bundle of Bundles
    private Map<HealthDataType, Bundle> bundlesMap = new Hashtable<>();
    private HealthDataType[] types;

    public DefaultHealthDataBundle(Bundle[] bundles, HealthDataType[] types) {
        for (Bundle bundle: bundles) {
            if (bundle.getUserData(HealthDataType.class.getName()) != null) {
                bundle.setUserData(INDEX, 0);
                bundlesMap.put((HealthDataType)bundle.getUserData(HealthDataType.class.getName()), bundle);
            }
        }
        this.types = types;
    }

    public DefaultHealthDataBundle(Bundle bundle, HealthDataType type) {
        this(new Bundle[] {bundle}, new HealthDataType[] {type});
    }

    @Override
    public HealthDataType[] getHealthRecordTypes() {
        return types;
    }

    @Override
    public boolean hasNext(HealthDataType type) {
        Bundle bundle = bundlesMap.get(type);
        if (bundle == null)
            return false;
        else {
            int index = bundle.getUserInt(INDEX);
            return (index < bundle.getTotal());
        }
    }

    @Override
    public Resource next(HealthDataType type) {
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
    public int getTotal(HealthDataType type) {
        Bundle bundle = bundlesMap.get(type);
        if (bundle == null)
            return 0;
        else
            return bundle.getTotal();
    }

}
