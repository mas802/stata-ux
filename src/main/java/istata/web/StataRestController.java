package istata.web;

import istata.domain.ContentLine;
import istata.domain.StataResultLine;
import istata.domain.StataVarLine;
import istata.service.StataService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ContentLine start(
            @RequestParam(value = "cmd", defaultValue = "World") String command) {
        stataService.startStata();
        return new ContentLine(200, "OK");
    }
    
    @RequestMapping("/run")
    public ContentLine run(
            @RequestParam(value = "cmd", defaultValue = "World") String command) {
        return stataService.run(command);
    }

    @RequestMapping("/vars")
    public List<StataVarLine> vars(
            @RequestParam(value = "from", defaultValue = "-1") int from,
            @RequestParam(value = "to", defaultValue = "-1") int to) {
        return stataService.vars(from, to);
    }

    @RequestMapping("/results")
    public List<StataResultLine> list(
            @RequestParam(value = "from", defaultValue = "-1") int from,
            @RequestParam(value = "to", defaultValue = "-1") int to) {
        return stataService.resultLines(from, to);
    }


    @RequestMapping("/cmds")
    public List<StataResultLine> cmds(
            @RequestParam(value = "from", defaultValue = "-1") int from,
            @RequestParam(value = "to", defaultValue = "-1") int to) {
        return stataService.resultLines(from, to);
    }
    
    @RequestMapping("/clear")
    public void clear() {
        stataService.clear("");
    }

    @RequestMapping(value = "/cmdparse")
    public Map<String, Object> cmdparse(
            @RequestParam(value = "cmdprefix", defaultValue = "") String cmdprefix,
            @RequestParam(value = "cmd", defaultValue = "") String cmd,
            @RequestParam(value = "cmdexpr", defaultValue = "") String cmdexpr,
            @RequestParam(value = "cmdif", defaultValue = "") String cmdif,
            @RequestParam(value = "cmdopt", defaultValue = "") String cmdopt,
            @RequestParam(value = "cmdfull", defaultValue = "") String cmdfull,
            @RequestParam(value = "focus", defaultValue = "") String focus,
            @RequestParam(value = "pos", defaultValue = "-1") int pos) {

        StringBuilder sb = new StringBuilder( cmdfull );
        sb.insert(pos, "|");
        
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("data", sb.toString());
        return res;

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
            @RequestParam(value = "pos", defaultValue = "-1") int pos) {

        List<ContentLine> res = null;
        
        if ( focus.equals("") || focus.equals("cmd") ) {
            res = stataService.cmdFiltered(cmd, pos);
        } else {
            String[] s = cmdexpr.split(" ");
            res = stataService.varFiltered(s[s.length-1] + "*");
        }
        
        return res;

    }
}
