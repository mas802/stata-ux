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
package istata.utils;

import istata.interact.model.StataVar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

public class StataVarTest {

    @Test
    public void testDescribe() throws FileNotFoundException {

        File descfile = new File("src/test/resources/describe.log");

        Scanner scan;
        scan = new Scanner(descfile);
        scan.useDelimiter("\\Z");
        String r = scan.next();
        scan.close();

        List<StataVar> vars = new ArrayList<StataVar>();

        r = r.replaceAll(
                ".*?[\r|\n]+Con.*?[\r|\n]+  obs.*?[\r|\n]+.*?[\r|\n]+.*?[\r|\n]+",
                "");
        r = r.replaceAll(".*?[\r|\n]+variable name.*?[\r|\n]+", "");
        r = r.replaceAll(".*?---[\r|\n]+", "");
        int i = r.indexOf("Sorted by:");
        r = r.substring(0, i);

        r = r.replaceAll("[\r|\n]+[ ]+", " ");

        System.out.println(r);

        // r = r.replaceAll("\n(\\s*?)> ", "");
        // r = r.replaceAll("\n\\s(\\s*?)(\\S)", " $2");
        // r = r.replaceAll("(\n\\S*?)[\n]", "$1");

        String lines[] = r.split("[\r|\n]+");
        for (String l : lines) {
            if (l.length() > 8) {
                vars.add(new StataVar(l));
                // System.out.println( new StataVar(l) );
            }
        }

        // TODO there is no actual test here (?)

    }
}
