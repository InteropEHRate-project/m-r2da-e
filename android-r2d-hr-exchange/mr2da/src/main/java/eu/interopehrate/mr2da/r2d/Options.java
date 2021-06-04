/**
 Copyright 2021 Engineering S.p.A. (www.eng.it) - InteropEHRate (www.interopehrate.eu)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package eu.interopehrate.mr2da.r2d;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 *       Author: Engineering S.p.A. (www.eng.it)
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: represents a collection of instances of Options
 */
public class Options {
    private Map<OptionName, Option> opts = new Hashtable<>();

    public Options add(@NonNull OptionName name, @NonNull Object value) {
        opts.put(name, new Option(name, value));
        return this;
    }

    public Options add(@NonNull Option opt) {
        opts.put(opt.getName(), opt);
        return this;
    }

    public boolean hasOption(@NonNull OptionName name) {
        return opts.containsKey(name);
    }

    @Nullable
    public Option getByName(@NonNull OptionName name) {
        return opts.get(name);
    }

    @Nullable
    public Object getValueByName(@NonNull OptionName name) {
        if (opts.containsKey(name))
            return opts.get(name).getValue();

        return null;
    }

    public Iterator<Option> getOptions() {
        return opts.values().iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("opts=[");
        for(Option opt : opts.values())
            sb.append(opt.toString());
        sb.append("]");

        return sb.toString();
    }

}
