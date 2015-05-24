package istata.interact;

import istata.interact.model.StataVar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
// import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stata implements IStata {

    final static int bufferSize = 1024 * 1024;

    boolean init = false;

    private File path;
    private File dofile;
    private File domarker;
    private File endmarker;
    private File alivemarker;
    private File logfile;
    private File descfile;

    private File graphlogfile;
    private File graphfile;

    private File estlogfile;
    private File estfile;

    private String workingdir = null;

    // set default constructor to enforce Factory construction
    Stata(String initpath) throws IOException {
        if (initpath == null) {
            this.path = new File(System.getProperty("java.io.tmpdir"));
        } else {
            this.path = new File(initpath);
            this.path.mkdirs();
        }
        dofile = new File(path, "i.do");
        domarker = new File(path, "do.marker");
        endmarker = new File(path, "end.marker");
        alivemarker = new File(path, "alive.marker");
        logfile = new File(path, "i.log");
        descfile = new File(path, "describe.log");

        graphlogfile = new File(path, "graph.log");
        graphfile = new File(path, "graph.png");

        estlogfile = new File(path, "est.log");
        estfile = new File(path, "est.html");

        // System.out.println(logfile.getAbsolutePath());

        logfile.createNewFile();
    }

    public File logfile() {
        return logfile;
    }

    private boolean semidelm = false;

    private String lastvarlist = "";

    boolean init() {
        if (!init) {

            Thread u = new Thread(new TailThread(this));
            u.start();

            // TODO check if is alive
            init = true;
        }
        return init;
    }

    /*
     * run a single command, will be cleared for line breaks and end delimiters
     * ; and ///
     */
    synchronized private void _run(String cmd) {

        vars = null;

        // long start = System.currentTimeMillis();
        /*
         * System.out .println("start _run " + (System.currentTimeMillis() -
         * start));
         */

        // TODO, wrong place?
        if (cmd.endsWith(";")) {
            cmd = cmd.substring(0, cmd.length() - 1);
        }

        cmd = cmd.trim();
        cmd = cmd + "\n";

        // System.out.print("STATA ###" + cmd + "### in: ");
        // System.out.println(dofile.getAbsolutePath() + " "
        // + (System.currentTimeMillis() - start));

        if (alivemarker.exists()) {
            try {
                // TODO wait for the last command to clear (THIS SHOULD BE
                // ASYNC)
                // maybe could just timeout after 2 sec
                while (domarker.exists() || endmarker.exists()) {
                    Thread.sleep(100);
                }

                // write command to default do file
                FileWriter fw = new FileWriter(dofile);
                fw.append(cmd);
                // fw.append("// END\n");
                fw.close();

                Thread.sleep(10);

                // set marker to execute the do file
                FileWriter fwm = new FileWriter(domarker);
                fwm.append("do");
                fwm.close();

                Thread.sleep(10);

                // wait until the dofile is cleared by stata
                // and logfile is written
                while (domarker.exists() || !endmarker.exists()) {
                    Thread.sleep(100);
                }

                Thread.sleep(10);

                // remove the logfile (signaling that the pipe is clear)
                endmarker.delete();
                Thread.sleep(10);
                while (endmarker.exists()) {
                    Thread.sleep(100);
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            throw new StataNotRunningException("service not alive");
        }

        // System.out.println("end " + (System.currentTimeMillis() - start));

    }

    /*
     * (non-Javadoc)
     * 
     * @see mas.research.stata.IStata#run(java.lang.String)
     */
    public void run(String... cmds) {

        if (!semidelm) {
            for (String s : cmds) {
                s = s.replaceAll("\\\\\\\n", " ");
                // String[] t = s.split("\n");
                // for (String c : t) {
                _run(s);
                // }
            }
        } else {
            for (String s : cmds) {
                String[] t = s.split(";");
                for (String c : t) {
                    _run(c);
                }
            }
        }

    }

    private List<StataVar> vars = null;

    private static final Pattern pwdpattern = Pattern
            .compile("\n(.*?)(\\s*?)\\Z");;

    /*
     * (non-Javadoc)
     * 
     * @see mas.research.stata.IStata#getVars(java.lang.String)
     */
    public List<StataVar> getVars(String string, boolean force) {

        if (string != null && string.length() > 0) {
            throw new IllegalArgumentException("filter not implemented yet");
        }

        if (vars == null || force) {

            if (alivemarker.exists()) {
                try {
                    while (domarker.exists() || endmarker.exists()) {
                        Thread.sleep(100);
                    }

                    // write command to pipe file
                    FileWriter fwm = new FileWriter(domarker);
                    fwm.append("describe");
                    fwm.close();

                    Thread.sleep(10);

                    // wait until the dofile is cleared by stata
                    // and logfile is written
                    while (domarker.exists() || !endmarker.exists()) {
                        Thread.sleep(100);
                    }

                    while (!descfile.exists()) {
                        Thread.sleep(10);
                    }

                    Scanner scan;
                    scan = new Scanner(descfile);
                    scan.useDelimiter("\\Z");
                    String r = scan.next();
                    scan.close();

                    Matcher m = pwdpattern.matcher(r);
                    if (m.find()) {
                        this.workingdir = m.group(1);
                    }

                    // check if something has changed
                    if (force || !r.equals(lastvarlist)) {
                        vars = new ArrayList<StataVar>();

                        r = r.replaceAll(
                                ".*?[\r|\n]+Con.*?[\r|\n]+  obs.*?[\r|\n]+.*?[\r|\n]+.*?[\r|\n]+",
                                "");
                        r = r.replaceAll(".*?[\r|\n]+variable name.*?[\r|\n]+",
                                "");
                        r = r.replaceAll(".*?---[\r|\n]+", "");
                        int i = r.indexOf("Sorted by:");
                        r = r.substring(0, i);

                        // r = r.replaceAll("\n(\\s*?)> ", "");
                        // r = r.replaceAll("\n\\s(\\s*?)(\\S)", " $2");
                        // r = r.replaceAll("(\n\\S*?)[\n]", "$1");

                        String lines[] = r.split("[\r|\n]+");
                        for (String l : lines) {
                            if (l.length() > 8) {
                                vars.add(new StataVar(l));
                                // System.out.println( new StataVar(l) );
                            }
                        }
                        lastvarlist = r;
                    }

                    endmarker.delete();

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                throw new StataNotRunningException("service not alive");
            }
        }
        return vars;
    }

    /*
     * (non-Javadoc)
     * 
     * @see mas.research.stata.IStata#getGraph()
     */
    public File getGraph() {

        if (alivemarker.exists()) {
            try {
                while (domarker.exists() || endmarker.exists()) {
                    Thread.sleep(100);
                }

                // write command to pipe file
                FileWriter fwm = new FileWriter(domarker);
                fwm.append("graph");
                fwm.close();

                Thread.sleep(10);

                // wait until the dofile is cleared by stata
                // and logfile is written
                while (domarker.exists() || !endmarker.exists()) {
                    Thread.sleep(100);
                }

                while (!graphlogfile.exists()) {
                    Thread.sleep(10);
                }

                /*
                 * Scanner scan; scan = new Scanner(graphlogfile);
                 * scan.useDelimiter("\\Z"); String r = scan.next();
                 * scan.close();
                 */

                graphlogfile.delete();
                endmarker.delete();

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            throw new StataNotRunningException("service not alive");
        }
        return (graphfile.exists()) ? graphfile : null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see mas.research.stata.IStata#getGraph()
     */
    public File getEst() {

        if (alivemarker.exists()) {
            try {
                while (domarker.exists() || endmarker.exists()) {
                    Thread.sleep(100);
                }

                // write command to pipe file
                FileWriter fwm = new FileWriter(domarker);
                fwm.append("est");
                fwm.close();

                Thread.sleep(10);

                // wait until the dofile is cleared by stata
                // and logfile is written
                while (domarker.exists() || !endmarker.exists()) {
                    Thread.sleep(100);
                }

                while (!estlogfile.exists()) {
                    Thread.sleep(10);
                }

                /*
                 * Scanner scan; scan = new Scanner(graphlogfile);
                 * scan.useDelimiter("\\Z"); String r = scan.next();
                 * scan.close();
                 */

                estlogfile.delete();
                endmarker.delete();

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            throw new StataNotRunningException("service not alive");
        }
        return (estfile.exists()) ? estfile : null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see mas.research.stata.IStata#isReady()
     */
    public boolean isReady() {
        return true;
    }

    public File getGraphPath(String graph, String format) {
        return null;
    }

    public void destroy() {
        init = false;
    }

    public boolean isSemidelm() {
        return semidelm;
    }

    public void setSemidelm(boolean semidelm) {
        this.semidelm = semidelm;
    }

    public void clear() {

        if (alivemarker.exists()) {
            try {
                while (domarker.exists() || endmarker.exists()) {
                    Thread.sleep(100);
                }

                // write command to marker pipe file
                FileWriter fwm = new FileWriter(domarker);
                fwm.append("clear");
                fwm.close();

                Thread.sleep(10);

                // wait until the dofile is cleared by stata
                // and logfile is written
                while (domarker.exists() || !endmarker.exists()) {
                    Thread.sleep(100);
                }

                endmarker.delete();

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            throw new StataNotRunningException("service not alive");
        }
    }

    public String getWorkingdir() {
        if (vars == null) {
            getVars("", true);
        }
        return workingdir;
    }

    private Set<IStataListener> listeners = new HashSet<IStataListener>();

    public void addStataListener(IStataListener listener) {
        listeners.add(listener);
    }

    public void removeStataListener(IStataListener listener) {
        listeners.remove(listener);
    }

    class TailThread implements Runnable {

        public boolean _running = true;
        public int _updateInterval = 100;
        private Stata v;

        TailThread(Stata v) {
            this.v = v;
        }

        public void run() {

            File _file = v.logfile();
            long _filePointer = 0;
            byte[] buffer = new byte[bufferSize];

            try {
                while (_running) {

                    // System.out.println("check file");

                    Thread.sleep(_updateInterval);
                    long len = _file.length();
                    if (len < _filePointer) {
                        _filePointer = len;
                    } else if (len > _filePointer) {
                        if (listeners != null && listeners.size() > 0) {
                            // File must have had something added to it!
                            RandomAccessFile raf = new RandomAccessFile(_file,
                                    "r");
                            raf.seek(_filePointer);
                            StringBuilder sb = new StringBuilder();

                            int bytesRead = 0;
                            while ((bytesRead = raf.read(buffer)) > 0) {
                                sb.append(new String(Arrays.copyOf(buffer,
                                        bytesRead)));
                            }

                            _filePointer = raf.getFilePointer();
                            raf.close();

                            for (IStataListener l : listeners) {
                                l.handleUpdate(sb.toString());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                final Writer result = new StringWriter();
                final PrintWriter printWriter = new PrintWriter(result);
                e.printStackTrace(printWriter);
            }
            // dispose();
        }

    }
}
