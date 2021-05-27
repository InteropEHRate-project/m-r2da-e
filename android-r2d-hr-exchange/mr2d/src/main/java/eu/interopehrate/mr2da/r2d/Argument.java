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
 *  Description: couple of name / value used to represent a generic argument
 */

public class Argument {

    private ArgumentName name;
    private Object value;

    public Argument(ArgumentName name, Object value) {
        if (value == null)
            throw new IllegalArgumentException("the value of an Argument cannot be null.");

        this.name = name;
        this.value = value;
    }

    public ArgumentName getName() {
        return name;
    }

    public Argument setName(ArgumentName name) {
        this.name = name;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public String getValueAsString() {
        return (String)value;
    }

    public String[] getValueAsStringArray() {
        if (isArray())
            return (String[])value;
        else
            return new String[]{value.toString()};
    }

    public Argument setValue(Object value) {
        this.value = value;
        return this;
    }

    public boolean isArray() {
        return value.getClass().isArray();
    }

    @Override
    public String toString() {
        return "Argument{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
