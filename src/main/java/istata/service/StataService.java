package istata.service;

import istata.domain.CmdRepository;
import istata.domain.ContentLine;
import istata.domain.StataDoFile;
import istata.domain.StataResultLine;
import istata.domain.StataVarLine;
import istata.interact.IStata;
import istata.interact.IStataListener;
import istata.interact.StataFactory;
import istata.interact.StataNotRunningException;
import istata.interact.StataUtils;
import istata.interact.model.StataResult;
import istata.interact.model.StataVar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.CharUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

@Service
public class StataService implements IStataListener {

    @Autowired
    private CmdRepository cmdRepository;

    @Autowired
    private StataFactory stataFactory;

    @Autowired
    private VelocityEngine velocityEngine;

    /* holder for current results */
    private ArrayList<String> results;

    private Process stataProcess = null;

    /**
     * link up as a statalistener
     */
    @PostConstruct
    private void init() {
        stataFactory.addStataListener(this);
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
        cmd.setContent(command.trim());

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
     * get results from a certain range, returns an empty list if the range does
     * not contain lines (can be polled for updates)
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

    /**
     * obtain the list of potetial matches in the file system for a given input
     * 
     * @param filter
     * @param pos
     * @param from
     * @param to
     * @return
     */
    public List<ContentLine> filteredFiles(String filter, int pos, int from,
            int to) {
        int p = (pos == -1 || pos > filter.length()) ? filter.length() : pos;

        String before = filter.substring(0, p);

        List<ContentLine> result = new ArrayList<ContentLine>();

        /*
         * check for a dangling " before the position
         */
        int count = StataUtils.countChar(before, "\"");
        if (count % 2 != 0) {
            String workingdir = stataFactory.getInstance().getWorkingdir();

            int beginquote = before.lastIndexOf('"');
            String token = before.substring(beginquote + 1);

            // potential matching files (might be large?)
            List<File> potentials = new ArrayList<File>();

            File absFile = new File(token);

            final String endname;
            if (absFile.getName().equals("")) {
                endname = token;
            } else {
                endname = absFile.getName();
            }

            FilenameFilter fnf = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(endname);
                }
            };

            FilenameFilter all = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return !name.startsWith(".");
                }
            };

            if (!token.equals("") || absFile.isDirectory()) {
                // absolute file

                File parent = null;
                if (absFile.isDirectory()) {
                    parent = absFile;
                    fnf = all;
                } else {
                    parent = absFile.getParentFile();
                }
                if (parent != null) {
                    File[] fs = parent.listFiles(fnf);
                    if (fs != null)
                        potentials.addAll(Arrays.asList(fs));
                }
            }

            // absolute file
            File relFile = new File(workingdir, token);
            File parent = null;
            if (relFile.isDirectory()) {
                parent = relFile;
                fnf = all;
            } else {
                parent = relFile.getParentFile();
            }
            if (parent != null) {
                File[] fs = parent.listFiles(fnf);
                if (fs != null)
                    potentials.addAll(Arrays.asList(fs));
            }

            // work out if rest needs to be trimmed
            String after = filter.substring(p);

            int endquote = p;

            // check whether we are in an existing string
            int aftercount = StataUtils.countChar(after, "\"");
            if (aftercount % 2 != 0) {
                int firstquote = after.indexOf('"');
                endquote += firstquote + 1;
            }

            int i = 0;
            for (File f : potentials) {
                ContentLine srl = new ContentLine();
                Map<String, Object> model = new HashMap<String, Object>();

                String filename = f.getAbsolutePath();
                if (filename.startsWith(workingdir)) {
                    filename = filename.substring(workingdir.length() + 1);
                }
                filename = filename + ((f.isDirectory()) ? "/" : "");

                String repltext = filter.substring(0, beginquote + 1)
                        + filename + "\"" + filter.substring(endquote);

                try {
                    model.put("text", URLEncoder.encode(repltext, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                model.put("filename", filename);
                model.put("pos", beginquote + filename.length() + 1);
                model.put("from", from);
                model.put("to", to);

                String text = VelocityEngineUtils.mergeTemplateIntoString(
                        velocityEngine, "file.vm", "UTF-8", model);

                srl.setContent(text);
                srl.setLine(i++);
                result.add(srl);
            }

        }

        return result;
    }

    public List<ContentLine> suggest(String filter, int pos, int from, int to) {
        LinkedHashSet<ContentLine> res = new LinkedHashSet<ContentLine>();

        ArrayList<ContentLine> rescmd = new ArrayList<ContentLine>();
        {
            int i = 0;
            for (ContentLine cl : cmdRepository.findAll()) {
                if (cl.getContent().startsWith(filter)) {
                    ContentLine srl = new ContentLine();
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("cmd", cl);
                    model.put("from", from);
                    model.put("to", to);

                    String text = VelocityEngineUtils.mergeTemplateIntoString(
                            velocityEngine, "cmd.vm", "UTF-8", model);

                    srl.setContent(text);
                    srl.setLine(i++);
                    rescmd.add(srl);

                    /*
                     * if ( i>5 ) { break; }
                     */

                }
            }
        }

        Collections.reverse(rescmd);

        res.addAll(rescmd.subList(0, Math.min(10, rescmd.size())));

        List<ContentLine> out = new ArrayList<ContentLine>();

        try {
            IStata stata = stataFactory.getInstance();

            /*
             * get files
             */
            Collection<ContentLine> filesNames = filteredFiles(filter, pos,
                    from, to);
            res.addAll(filesNames);

            /*
             * get VARS, should be a mothod call probably
             */

            // current token
            StringBuilder token = new StringBuilder("");
            StringBuilder rest = new StringBuilder(filter);
            int p = (pos == -1 || pos > filter.length()) ? filter.length()
                    : pos;
            char ch = 'x';
            while (p > 0
                    && (CharUtils
                            .isAsciiAlphanumeric(ch = filter.charAt(p - 1)) || ch == '_')) {
                token.insert(0, ch);
                rest.deleteCharAt(p - 1);
                p--;
            }

            // remove rest of potential token
            while (rest.length() > 0
                    && p > 0
                    && p < rest.length()
                    && (CharUtils.isAsciiAlphanumeric(rest.charAt(p)) || rest
                            .charAt(p) == '_')) {
                rest.deleteCharAt(p);
            }

            String t = token.toString();

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
                            + URLEncoder.encode(cl, "UTF-8") + "\" " + ", "
                            + (p + vname.length() + 1) + ");' "
                            + "ondblclick='handle(\"sidebardblclick\", \""
                            + URLEncoder.encode(cl, "UTF-8") + "\"" + ", " + -1
                            + ");'>" + vname + "</div>");
                    res.add(srl);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (StataNotRunningException e) {
            StataVarLine srl = new StataVarLine();
            srl.setLine(1);
            srl.setContent("<div class='sidebaritem error' >"
                    + "Stata not running, you can try to start "
                    + "an instance by clicking "
                    + "<a target='_blank' href='/start'>here</a>" + "</div>");
            out.add(srl);
        }

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

    /**
     * load a do file
     */
    public StataDoFile loadDoFile(String path) {
        StataDoFile dofile = new StataDoFile();

        File file = new File(path);

        dofile.setPath(path);
        dofile.setTimestamp(file.lastModified());

        Scanner scan = null;
        try {
            scan = new Scanner(file, "utf-8");
            scan.useDelimiter("\\Z");
            dofile.setContent(scan.next());
        } catch (FileNotFoundException e) {
            // FIXME I guess this should be handled more elegantely
            throw new RuntimeException("issues reading file " + e.getMessage());
        } finally {
            if (scan != null)
                scan.close();
        }

        return dofile;
    }

    /**
     * save a do file
     */
    public ContentLine saveDoFile(StataDoFile dofile) {

        // FIXME, check wether file has same lastmodified/version
        // File file = new File( dofile.getName() );

        // todo might go into the database as well for the history

        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(dofile.getPath()), "UTF-8"));
            out.write(dofile.getContent());
            out.close();
        } catch (IOException e) {
            // FIXME, certainly not ideal
            e.printStackTrace();
            return new ContentLine(400, e.getMessage());
        }

        return new ContentLine(200, "OK");
    }

    /**
     * save and run do file
     */
    public ContentLine saveAndRunDoFile(StataDoFile dofile) {

        ContentLine cl = saveDoFile(dofile);

        if (cl.getLine() == 200) {
            return run("do \"" + dofile.getPath() + "\"");
        } else {
            return cl;
        }
    }

    /*
     * not sure if a good solution, but keep the end of the line as a field
     * variable if an update does not yield a newline char
     */
    private String cacheUpdate = "";

    /*
     * (non-Javadoc)
     * 
     * @see istata.interact.IStataListener#handleUpdate(java.lang.String)
     */
    @Override
    public void handleUpdate(String update) {
        update = cacheUpdate + update;

        cacheUpdate = "";
        String[] arr = update.split("\n\\{txt\\}");
        for (int i = 0; i < arr.length - 1; i++) {
            addToResults(arr[i]);
        }
        String last = arr[arr.length - 1];
        if (last.endsWith("\n")) {
            addToResults(last);
        } else {
            cacheUpdate = last;
        }
    }

    // utility method to check results for sanity
    private boolean addToResults(String s) {
        if (s.endsWith("end of do-file\n{smcl}")) {
            s = s.substring(0, s.length() - 21);
        }
        return (s.length() > 0) ? results.add(s) : false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * istata.interact.IStataListener#handleResult(istata.interact.model.StataResult
     * )
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
