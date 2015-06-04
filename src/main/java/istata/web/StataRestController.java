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
package istata.web;

import istata.domain.ContentLine;
import istata.domain.StataDoFile;
import istata.service.StataService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StataRestController {

    private StataService stataService;

    @Autowired
    public StataRestController(StataService stataService) {
        this.stataService = stataService;
    }

    @RequestMapping("/start")
    public ContentLine start() {
        stataService.startStata();
        return new ContentLine(200, "OK");
    }

    @RequestMapping("/run")
    public ContentLine run(@RequestParam(value = "cmd") String command) {
        return stataService.run(command);
    }

    @RequestMapping("/results")
    public List<ContentLine> list(
            @RequestParam(value = "from", defaultValue = "-1") int from,
            @RequestParam(value = "to", defaultValue = "-1") int to) {
        return stataService.resultLines(from, to);
    }

    @RequestMapping(value = "/savedofile", method = RequestMethod.POST)
    public ContentLine savedofile(@ModelAttribute StataDoFile dofile) {
        return stataService.saveDoFile(dofile);
    }

    @RequestMapping(value = "/saveandrundofile", method = RequestMethod.POST)
    public ContentLine saveAndRunDoFile(@ModelAttribute StataDoFile dofile) {
        return stataService.saveAndRunDoFile(dofile);
    }

    @RequestMapping("/cmds")
    public List<ContentLine> cmds(
            @RequestParam(value = "from", defaultValue = "-1") int from,
            @RequestParam(value = "to", defaultValue = "-1") int to) {
        return stataService.resultLines(from, to);
    }

    @RequestMapping("/clear")
    public void clear() {
        stataService.clear("");
    }

    @RequestMapping(value = "/suggest")
    public List<ContentLine> suggest(
            @RequestParam(value = "cmdprefix", defaultValue = "") String cmdprefix,
            @RequestParam(value = "cmd", defaultValue = "") String cmd,
            @RequestParam(value = "cmdexpr", defaultValue = "") String cmdexpr,
            @RequestParam(value = "cmdif", defaultValue = "") String cmdif,
            @RequestParam(value = "cmdopt", defaultValue = "") String cmdopt,
            @RequestParam(value = "cmdfull", defaultValue = "") String cmdfull,
            @RequestParam(value = "focus", defaultValue = "") String focus,
            @RequestParam(value = "pos", defaultValue = "-1") int pos,
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "end", defaultValue = "-1") int end) {

        List<ContentLine> res = null;

        if (focus.equals("") || focus.equals("cmd")) {
            res = stataService.suggest(cmd, pos, start, end);
        }

        return res;
    }

}
