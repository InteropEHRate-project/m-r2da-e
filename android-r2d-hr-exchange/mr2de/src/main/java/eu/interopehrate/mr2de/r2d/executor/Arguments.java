package eu.interopehrate.mr2de.r2d.executor;

import java.util.HashMap;
import java.util.Map;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: represents a collection of instances of Argument
 */
public class Arguments {

    private Map<String, Argument> args = new HashMap<String, Argument>();

    public Arguments add(String name, Object value) {
        args.put(name, new Argument(name, value));
        return this;
    }

    public Argument getByName(String name) {
        return args.get(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("args=[");
        for(Argument arg : args.values())
            sb.append(arg.toString());
        sb.append("]");

        return sb.toString();
    }
}
