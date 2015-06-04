/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package istata.interact.model;

public class StataVar {

    private String name;
    private String type;
    private String format;
    private String valueLabel;
    private boolean hasNotes;
    private String label;

    public StataVar(String describe) {
        // System.out.println(describe);
        this.name = describe.replaceFirst("(^\\S*?)\\s(.*?)$", "$1");
        // System.out.println(name);
        describe = describe.substring(this.name.length()).trim();
        int len = describe.length();
        int start = 0;
        int end = 7;
        this.type = describe.substring(0, 7).trim();
        start = end;
        end = Math.min(19, len);
        this.format = describe.substring(start, end).trim();
        start = end;
        end = Math.min(27, len);
        this.valueLabel = describe.substring(start, end).trim();
        start = end;
        end = Math.min(30, len);
        this.hasNotes = (describe.substring(start, end).trim().equals(". *"));
        start = end;
        this.label = describe.substring(start);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getValueLabel() {
        return valueLabel;
    }

    public void setValueLabel(String valueLabel) {
        this.valueLabel = valueLabel;
    }

    public boolean isHasNotes() {
        return hasNotes;
    }

    public void setHasNotes(boolean hasNotes) {
        this.hasNotes = hasNotes;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return name + "|\t|" + type + "|\t|" + format + "|\t|" + valueLabel
                + "|\t|" + hasNotes + "|\t|" + label;
    }
}
