package istata.service;

import istata.domain.CmdRepository;
import istata.domain.ContentLine;
import istata.domain.StataResultLine;
import istata.domain.StataVarLine;
import istata.interact.IStata;
import istata.interact.IStataListener;
import istata.interact.StataFactory;
import istata.interact.StataUtils;
import istata.interact.model.StataResult;
import istata.interact.model.StataVar;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.CharUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StataService implements IStataListener {

    @Autowired
    private CmdRepository cmdRepository;

    @Autowired
    private StataFactory stataFactory;
 
    /* holder for current results */
    private ArrayList<String> results;
  
    private Process stataProcess = null;

    /**
     * link up as a statalistener
     */
    @PostConstruct 
    private void init() {
        stataFactory.addStataListener( this );
    }

    
    public StataService() {
        results = new ArrayList<String>();
    }

    /*
     * run a stata command (can be multiline)
     */
    public ContentLine run(String command) {
        IStata stata = stataFactory.getInstance();
        stata.run(command);

        ContentLine cmd = new ContentLine();
        cmd.setContent(command);

        cmdRepository.save(cmd);

        return new ContentLine(1, "ran " + command);
    }

    /*
     * get the complete results as a html page
     * 
     * currently deprecated in favor of a more ajax approach below
     */
    @Deprecated
    public String results(String command) {
        StringBuilder sb = new StringBuilder();

        // header
        sb.append("<html><head><meta http-equiv=\"refresh\" content=\"1\"></head><body><pre>");

        for (int i = Math.max(0, results.size() - 80); i < results.size(); i++) {
            sb.append(StataUtils.smcl2html(results.get(i).toString(), false));
        }

        return sb.toString();
    }

    /*
     * get results from a certain range, returns an empty list if the range
     * does not contain lines (can be polled for updates)
     */
    public List<StataResultLine> resultLines(int from, int to) {
        if (from < 0) {
            from = Math.max(0, results.size() - 80);
        }

        if (to < 0) {
            to = results.size();
        }

        ArrayList<StataResultLine> res = new ArrayList<StataResultLine>();

        for (int i = from; i < to; i++) {
            StataResultLine srl = new StataResultLine();
            srl.setLine(i);
            srl.setContent(StataUtils.smcl2html(results.get(i).toString(),
                    false));
            res.add(srl);
        }

        return res;
    }

    /*
     * returns variables from a certain range
     */
    public List<StataVarLine> vars(int from, int to) {
        IStata stata = stataFactory.getInstance();
        List<StataVar> vars = stata.getVars("", false);

        if (from < 0) {
            from = 0;
        }

        if (to < 0) {
            to = Math.min(80, vars.size());
        }

        ArrayList<StataVarLine> res = new ArrayList<StataVarLine>();

        for (int i = from; i < to; i++) {
            StataVarLine srl = new StataVarLine();
            srl.setLine(i);
            srl.setContent("<p>" + vars.get(i).getName() + "</p>");
            res.add(srl);
        }

        return res;
    }

    
    public List<ContentLine> varFiltered(String filter) {
        IStata stata = stataFactory.getInstance();
        List<StataVar> list = stata.getVars(filter, false);

        ArrayList<ContentLine> res = new ArrayList<ContentLine>();

        for (int i = 0; i < list.size(); i++) {
            StataVarLine srl = new StataVarLine();
            srl.setLine(i);
            srl.setContent("<p>" + list.get(i).getName() + "</p>");
            res.add(srl);
        }

        return res;
    }

    public List<ContentLine> cmdFiltered(String filter, int pos) {
        LinkedHashSet<ContentLine> res = new LinkedHashSet<ContentLine>();

        // current token
        StringBuilder token = new StringBuilder("");
        StringBuilder rest = new StringBuilder(filter);
        int p = (pos==-1 || pos>filter.length())?filter.length():pos;
        char ch = 'x';
        while (p > 0 && CharUtils.isAsciiAlpha(ch = filter.charAt(p - 1))) {
            token.insert(0, ch);
            rest.deleteCharAt(p - 1);
            p--;
        }

        // remove rest of potential token
        while (rest.length() > 0 && p>0 && p < rest.length()
                && CharUtils.isAsciiAlpha(rest.charAt(p))) {
            rest.deleteCharAt(p);
        }

        String t = token.toString();

        IStata stata = stataFactory.getInstance();
        // List<StataVar> list = stata.getVars(token.toString(), false);
        List<StataVar> list = new ArrayList<StataVar>();
        List<StataVar> listfull = stata.getVars("", false);
        if (t.length() > 0) {
            for (StataVar sv : listfull) {
                if (sv.getName().startsWith(t)) {
                    list.add(sv);
                }
            }
        } else {
            list = listfull;
        }

        for (int i = 0; i < list.size(); i++) {
            StataVarLine srl = new StataVarLine();
            srl.setLine(i + 100);
            String vname = list.get(i).getName();
            String cl = new StringBuilder(rest).insert(p, " ")
                    .insert(p, vname).toString();
            try {
                srl.setContent("<div class='sidebaritem varitem' "
                        + "onclick='handle(\"sidebarclick\", \" "
                        + URLEncoder.encode(cl, "UTF-8") + "\" "+
                                ", "+ p+vname.length()+1 +");' "
                        + "ondblclick='handle(\"sidebardblclick\", \""
                        + URLEncoder.encode(cl, "UTF-8") + "\""+
                        ", "+ -1 +");'>"
                        + vname + "</div>");
                res.add(srl);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        {
            int i = 0;
            for (ContentLine cl : cmdRepository.findAll()) {
                if (cl.getContent().startsWith(filter)) {
                    ContentLine srl = new ContentLine();
                    srl.setLine(i++);
                    try {
                        srl.setContent("<div class='sidebaritem full' "
                                + "onclick='handle(\"sidebarclick\", \""
                                + URLEncoder.encode(cl.getContent(), "UTF-8") + "\""+
                                ", "+ -1 +");' "
                                + "ondblclick='handle(\"sidebardblclick\", \""
                                + URLEncoder.encode(cl.getContent(), "UTF-8")
                                 + "\", "+ -1 +");'>" + cl.getContent() + "</div>");
                        res.add(srl);
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

 
        List<ContentLine> out = new ArrayList<ContentLine>();
        out.addAll(res);
        return out;
    }

    /*
     * return all vars as beans
     */
    public List<StataVar> vars(String command) {
        IStata stata = stataFactory.getInstance();
        List<StataVar> vars = stata.getVars(command, false);
        return vars;
    }

    /*
     * not sure if a good solution, but keep the end of the line as a
     * field variable if an update does not yield new
     */
    private String cacheUpdate = "";
    
    /*
     * (non-Javadoc)
     * @see istata.interact.IStataListener#handleUpdate(java.lang.String)
     */
    @Override
    public void handleUpdate(String update) {
        update = cacheUpdate + update;
        
        cacheUpdate = "";
        String[] arr = update.split("\n\\{txt\\}");
        for (int i = 0; i < arr.length-1; i++) {
            addToResults(arr[i]);
        }
        String last = arr[arr.length-1];
        if ( last.endsWith("\n")) {
            addToResults(last);
        } else {
            cacheUpdate = last;
        }
    }

    // utility method to check results for sanity
    private boolean addToResults( String s ) {
        if ( s.endsWith("end of do-file\n{smcl}")) {
            s = s.substring(0, s.length()-21);
        }
        return (s.length()>0)?results.add(s):false;
    }
    
    /*
     * (non-Javadoc)
     * @see istata.interact.IStataListener#handleResult(istata.interact.model.StataResult)
     */
    @Override
    public void handleResult(StataResult result) {
        // TODO Auto-generated method stub

    }

    /**
     * method to get a graph file
     * 
     * @return graph file
     */
    public File graph() {
        IStata stata = stataFactory.getInstance();
        return stata.getGraph();
    }

    /**
     * method to get a estimation results file
     * 
     * @return est file
     */
    public File est() {
        IStata stata = stataFactory.getInstance();
        return stata.getEst();
    }

    /**
     * trigger a clear of the stata interaction
     */
    public void clear(String name) {
        IStata stata = stataFactory.getInstance();
        stata.clear();
    }

    /**
     * start a stata program instance (under development)
     */
    public void startStata() {
        if (stataProcess != null) {
            stataProcess.destroy();
        }

        String[] stataProgs = new String[] { "/Applications/Stata/" };
        File stataexe = StataUtils.resolveStataPath(stataProgs,
                System.getProperty("os.name", "generic"));
        try {
            stataProcess = StataUtils.runInteract(stataexe.getAbsolutePath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
