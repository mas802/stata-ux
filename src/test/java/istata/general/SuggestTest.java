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

import istata.domain.CmdRepository;
import istata.domain.ContentLine;
import istata.interact.IStata;
import istata.interact.StataBusyException;
import istata.interact.StataFactory;
import istata.interact.model.StataVar;
import istata.service.StataService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SuggestTest {

    @Mock
    private StataFactory stataFactory;

    @InjectMocks
    private StataService service = new StataService();

    @Mock
    private CmdRepository cmdRepository;

    @Mock
    private IStata stata;

    @Mock
    private VelocityEngine velocityEngine;

    @Test
    public void someTest() throws StataBusyException {
        // Mockito.stubVoid(stataFactory.addStataListener(service));

        String[] cmd = { "reg this", "rag that", "sum me", "sum me" };
        List<ContentLine> cmds = new ArrayList<ContentLine>();
        for (String s : cmd) {
            ContentLine cl = new ContentLine();
            cl.setContent(s);
            cmds.add(cl);
        }

        String[] var = { "one one      |", "one1  one1     |",
                "summary summary      |" };
        List<StataVar> vars = new ArrayList<StataVar>();
        for (String s : var) {
            StataVar cl = new StataVar(s + "");
            vars.add(cl);
        }

        Mockito.when(cmdRepository.findAll()).thenReturn(cmds);
        Mockito.when(stataFactory.getInstance()).thenReturn(stata);
        Mockito.when(stata.getVars("", false)).thenReturn(vars);

        List<ContentLine> list = service.suggest("sum", -1, 0, -1);

        for (ContentLine cl : list) {
            System.out.println(cl.getContent());
        }

        Assert.assertEquals(1, list.size());
    }

    @Test
    public void removeDuplicatesTest() throws StataBusyException {
        // Mockito.stubVoid(stataFactory.addStataListener(service));

        String[] cmd = { "reg this", "rag that", "sum me", "sum me" };
        List<ContentLine> cmds = new ArrayList<ContentLine>();
        for (String s : cmd) {
            ContentLine cl = new ContentLine();
            cl.setContent(s);
            cmds.add(cl);
        }

        String[] var = { "one one      |", "one1  one1     |",
                "summary summary      |" };
        List<StataVar> vars = new ArrayList<StataVar>();
        for (String s : var) {
            StataVar cl = new StataVar(s + "");
            vars.add(cl);
        }

        Mockito.when(cmdRepository.findAll()).thenReturn(cmds);
        Mockito.when(stataFactory.getInstance()).thenReturn(stata);
        Mockito.when(stata.getVars("", false)).thenReturn(vars);

        List<ContentLine> list = service.suggest("sum m", -1, 0, -1);

        for (ContentLine cl : list) {
            System.out.println("x:" + cl.getContent());
        }

        Assert.assertEquals(1, list.size());
    }

    @Test
    public void filesTest() throws StataBusyException {
        // Mockito.stubVoid(stataFactory.addStataListener(service));

        List<ContentLine> cmds = new ArrayList<ContentLine>();
        List<StataVar> vars = new ArrayList<StataVar>();

        Mockito.when(cmdRepository.findAll()).thenReturn(cmds);
        Mockito.when(stataFactory.getInstance()).thenReturn(stata);
        Mockito.when(stata.getVars("", false)).thenReturn(vars);
        Mockito.when(stata.getWorkingdir()).thenReturn(
                new File("src/").getAbsolutePath());
        // Mockito.when( velocityEngine).thenReturn( new
        // File("src/").getAbsolutePath() );

        System.out.println(new File(".").getAbsolutePath());

        List<ContentLine> list = service.suggest("cd \"test/jav", -1, 0, -1);

        for (ContentLine cl : list) {
            System.out.println("x:" + cl.getContent());
        }

        Assert.assertEquals(1, list.size());
    }

    @Test
    public void filesTestAbs() throws StataBusyException {
        // Mockito.stubVoid(stataFactory.addStataListener(service));

        List<ContentLine> cmds = new ArrayList<ContentLine>();
        List<StataVar> vars = new ArrayList<StataVar>();

        Mockito.when(cmdRepository.findAll()).thenReturn(cmds);
        Mockito.when(stataFactory.getInstance()).thenReturn(stata);
        Mockito.when(stata.getVars("", false)).thenReturn(vars);
        Mockito.when(stata.getWorkingdir()).thenReturn(
                new File("src/").getAbsolutePath());
        // Mockito.when( velocityEngine).thenReturn( new
        // File("src/").getAbsolutePath() );

        System.out.println(new File(".").getAbsolutePath());

        List<ContentLine> list = service.suggest("cd \"/Users", -1, 0, -1);

        for (ContentLine cl : list) {
            System.out.println("x:" + cl.getContent());
        }

        Assert.assertEquals(1, list.size());
    }

    @Test
    public void filesTestTilde() throws StataBusyException {
        // Mockito.stubVoid(stataFactory.addStataListener(service));

        List<ContentLine> cmds = new ArrayList<ContentLine>();
        List<StataVar> vars = new ArrayList<StataVar>();

        Mockito.when(cmdRepository.findAll()).thenReturn(cmds);
        Mockito.when(stataFactory.getInstance()).thenReturn(stata);
        Mockito.when(stata.getVars("", false)).thenReturn(vars);
        Mockito.when(stata.getWorkingdir()).thenReturn(
                new File("src/").getAbsolutePath());
        // Mockito.when( velocityEngine).thenReturn( new
        // File("src/").getAbsolutePath() );

        System.out.println(new File(".").getAbsolutePath());

        String s = "cd \"~/Dropbox/PROJECTS/hrv and happiness/an"; // "cd \"~/Desk"
        List<ContentLine> list = service.suggest(s, -1, 0, -1);

        for (ContentLine cl : list) {
            System.out.println("x:" + cl.getContent());
        }

        Assert.assertEquals(1, list.size());
    }
}
