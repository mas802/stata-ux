package istata.web;

import istata.service.StataService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HtmlController {

    private StataService stataService;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    public HtmlController(StataService stataService) {
        this.stataService = stataService;
    }

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

    @RequestMapping("/est")
    public void est(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        File f = stataService.est();

        InputStream in = new FileInputStream(f);
        IOUtils.copy(in, response.getOutputStream());
    }
}
