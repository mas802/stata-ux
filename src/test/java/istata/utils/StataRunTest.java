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

import istata.interact.IStata;
import istata.interact.IStataListener;
import istata.interact.StataFactory;
import istata.interact.StataNotRunningException;
import istata.interact.model.StataResult;

import org.junit.Test;

public class StataRunTest {

    @Test
    public void testUpdate() {

        final long start = System.currentTimeMillis();

        IStata stata = new StataFactory().getInstance();

        IStataListener l = new IStataListener() {

            @Override
            public void handleUpdate(String update) {

                System.out.println(update);
                System.out.println(System.currentTimeMillis() - start);
            }

            @Override
            public void handleResult(StataResult result) {
                // TODO Auto-generated method stub

            }
        };

        stata.addStataListener(l);

        try {
            stata.run("sysuse auto");
        } catch (RuntimeException e) {
            // make sure test is succesful
            System.err.println("Runtime Error " + e.getMessage());
        }

    }

    @Test
    public void testWorkingdir() {

        // final long start = System.currentTimeMillis();

        IStata stata = new StataFactory().getInstance();

        try {
            stata.getVars("", true);
            String wd = stata.getWorkingdir();
            System.out.println(wd);
        } catch (StataNotRunningException e) {
            // make sure test is succesful
            System.err.println("Runtime Error " + e.getMessage());
        }

    }
}
