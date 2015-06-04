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

import istata.service.StataService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

@Controller
public class HtmlController {

    private StataService stataService;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    public HtmlController(StataService stataService) {
        this.stataService = stataService;
    }

    @Autowired
    private VelocityEngine velocityEngine;

    @RequestMapping(value = "results", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String results() {
        return stataService.results("");
    }

    @RequestMapping("/graph")
    public void graph(HttpServletResponse response) throws IOException {
        response.setContentType("image/png");

        File f = stataService.graph();

        InputStream in = new FileInputStream(f);
        IOUtils.copy(in, response.getOutputStream());
    }
    

    @RequestMapping(value={"/graph **", "/graph **/**"})
    public void graphs(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/png");

        String path = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        
        path = path.substring(7);
        
        stataService.run("graph display " + path);
        
        File f = stataService.graph();

        InputStream in = new FileInputStream(f);
        IOUtils.copy(in, response.getOutputStream());
    }

    @RequestMapping("/est")
    public void est(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        File f = stataService.est();

        InputStream in = new FileInputStream(f);
        IOUtils.copy(in, response.getOutputStream());
    }

    
    @RequestMapping(value={"/edit \"**", "/edit \"**/**"})
    @ResponseBody
    public String edit(HttpServletRequest request, Map<String, Object> model) {
        
        String path = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        
        path = path.substring(7, path.length()-1);
        
        model.put("content", stataService.loadDoFile(path).getContent());
        model.put("title", path);
        
        return VelocityEngineUtils.mergeTemplateIntoString(
                velocityEngine, "edit.vm", "UTF-8", model);
    }


    @RequestMapping(value={"/help **", "/help **/**"})
    public String help(HttpServletRequest request, Map<String, Object> model) {
        
        String path = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        
        path = path.substring(6);

        return "redirect:http://www.stata.com/help.cgi?" + path;
    }

}
