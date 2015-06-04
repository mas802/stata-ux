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

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StataResult {

    private String cmd;
    private String content;
    private String error = "";
    private String graph = null;

    public StataResult(String cmd, String content) {
        this.cmd = cmd;
        this.content = content;
        // this.error = content.replaceAll(".*?(r\\(\\d*?\\);)*?.*?", "$1");
        Pattern p = Pattern
                .compile(".*?[\r|\n]+(r[(]\\d+?[)];)*?($|[\r|\n]+).*?");
        Matcher m = p.matcher(content);
        if (m.find()) {
            this.error = m.group(1);
        }

        Pattern pg = Pattern.compile(".*?[\r|\n]+[(]file (.*?) saved[)].*?");
        Matcher mg = pg.matcher(content);
        if (mg.find()) {
            this.graph = (this.graph == null) ? mg.group(1) : this.graph + ","
                    + mg.group(1);
            File g = new File(this.graph);
            this.graph = g.getAbsolutePath();
        }
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getError() {
        return (error == null) ? "" : error;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public String getGraph() {
        return (graph == null) ? "" : graph;
    }

}
