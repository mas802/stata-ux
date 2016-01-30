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
package istata.general;

import istata.domain.EstBean;
import istata.service.StataService;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class EstBuilderTest {

    @Test
    public void handleUpdatesTest() {

        StataService service = new StataService();

        EstBean e1 = new EstBean();
        e1.setCommand("cmd1");

        EstBean e2 = new EstBean();
        e2.setCommand("<i> mod");

        EstBean e3 = new EstBean();
        e3.setCommand("<i0>");

        EstBean e4 = new EstBean();
        e4.setCommand("<i>");

        List<EstBean> result = service.realiseEstDo(Arrays.asList(e1, e2, e3,
                e4));

        for (EstBean e : result) {
            System.out.println(e.getCommand());

        }
    }
}
