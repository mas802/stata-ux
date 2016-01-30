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

import static org.junit.Assert.assertEquals;
import istata.interact.StataUtils;

import org.junit.Test;

public class ScmlTest {

    @Test
    public void test() {
        String s = "{res}{sf}{ul off}{txt}\n"
                + "{com}. use \"dta/hrv_happy_analysis_20150514.dta\", clear\n"
                + "{txt}";

        System.out.println(StataUtils.smcl2plain(s, false));
    }

    @Test
    public void testHline() {

        final long start = System.currentTimeMillis();

        String r = null;
        for (int i = 0; i < 1; i++) {
            String s = "{res}{sf}{ul off}{txt}\n" + "{com}. {hline 100}"
                    + "{txt}";

            r = StataUtils.smcl2plain(s, false);
        }
        System.out.println(r.length() + " - \n\n" + r);

        System.out.println(System.currentTimeMillis() - start);

        assertEquals(104, r.length());

    }

    @Test
    public void testSpace() {

        final long start = System.currentTimeMillis();

        String r = null;
        for (int i = 0; i < 1; i++) {
            String s = "{res}{sf}{ul off}{txt}\n" + "{com}. {space 100}"
                    + "{txt}";

            r = StataUtils.smcl2plain(s, false);
        }
        System.out.println(r.length() + " - \n\n" + r);

        System.out.println(System.currentTimeMillis() - start);

        assertEquals(104, r.length());

    }

    @Test
    public void testHlite() {

        final long start = System.currentTimeMillis();

        String r = null;
        for (int i = 0; i < 1; i++) {
            String s = "{res}{sf}{ul off}{txt}\n" + "{com}. {hilite:harmby}"
                    + "{txt}";

            r = StataUtils.smcl2plain(s, false);
        }
        System.out.println(r.length() + " - \n\n" + r);

        System.out.println(System.currentTimeMillis() - start);

        assertEquals(10, r.length());

    }

    @Test
    public void testHelp() {

        final long start = System.currentTimeMillis();

        String r = null;
        for (int i = 0; i < 1; i++) {
            String s = "{res}{sf}{ul off}{txt}\n"
                    + "{com}. {help j_robustsingular##|_new:F(14,2342)}= ."
                    + "{txt}";

            r = StataUtils.smcl2plain(s, false);
        }
        System.out.println(r.length() + " - \n\n" + r);

        System.out.println(System.currentTimeMillis() - start);

        assertEquals(17, r.length());

    }

    @Test
    public void testCmd() {

        final long start = System.currentTimeMillis();

        String r = null;
        for (int i = 0; i < 1; i++) {
            String s = "{res}{sf}{ul off}{txt}\n"
                    + "{com}. -{cmd:ssc describe z}-" + "{txt}";

            r = StataUtils.smcl2plain(s, false);
        }
        System.out.println(r.length() + " - \n\n" + r);

        System.out.println(System.currentTimeMillis() - start);

        assertEquals(20, r.length());

    }

    // Iteration 1:{space 3}log likelihood = {res:-335.50678}
    @Test
    public void testRes() {

        final long start = System.currentTimeMillis();

        String r = null;
        for (int i = 0; i < 1; i++) {
            String s = "Iteration 1:{space 3}log likelihood = {res:-335.50678}";
            r = StataUtils.smcl2plain(s, false);
        }
        System.out.println(r.length() + " - \n\n" + r);

        System.out.println(System.currentTimeMillis() - start);

        assertEquals(43, r.length());

    }
}
