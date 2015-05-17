package istata.interact.model;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StataResult {

    private String cmd;
    private String content;
    private String error = "";
    private String graph = null;
    
    public StataResult( String cmd, String content) {
        this.cmd = cmd;
        this.content = content;
//      this.error = content.replaceAll(".*?(r\\(\\d*?\\);)*?.*?", "$1");
        Pattern p = Pattern.compile(".*?[\r|\n]+(r[(]\\d+?[)];)*?($|[\r|\n]+).*?" );
        Matcher m = p.matcher(content);
        if (m.find()) {
            this.error = m.group(1);
        }
        
        Pattern pg = Pattern.compile(".*?[\r|\n]+[(]file (.*?) saved[)].*?");
        Matcher mg = pg.matcher(content);
        if (mg.find()) {
            this.graph = (this.graph==null)?mg.group(1):this.graph + ","+ mg.group(1);
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
        return (error==null)?"":error;
    }


    public void setGraph(String graph) {
        this.graph = graph;
    }


    public String getGraph() {
        return (graph==null)?"":graph;
    }
 
    
}
