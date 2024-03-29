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

/**
 *      Author: Engineering S.p.A. (www.eng.it)
 *     Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: couple of name / value used to represent an option used to execute a
 *  query on FHIR (sort, count, etc. etc.)
 */

public class Option {
    private OptionName name;
    private Object value;

    public Option(OptionName name, Object value) {
        if (value == null)
            throw new IllegalArgumentException("the value of an Argument cannot be null.");

        this.name = name;
        this.value = value;
    }

    public OptionName getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public String getValueAsString() {
        return value.toString();
    }

    public Integer getValueAsInteger() {
        return (Integer)value;
    }

    public Boolean getValueAsBoolean() {
        return (Boolean)value;
    }


    public enum Sort {
        SORT_ASCENDING_DATE,
        SORT_DESCENDING_DATE
    }

    public enum Include {
        INCLUDE_HAS_MEMBER,
        INCLUDE_RESULTS,
        INCLUDE_MEDIA
    }

}
