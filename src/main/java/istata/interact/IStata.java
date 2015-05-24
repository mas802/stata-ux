package istata.interact;

import istata.interact.model.StataVar;

import java.io.File;
import java.util.List;

public interface IStata {

    public abstract void run(String... cmds);

    public abstract File logfile();

    /*
     * get varlist, returns null if the var list has not changed since the last
     * call, unless force is true.
     */
    public abstract List<StataVar> getVars(String string, boolean force);

    /*
     * get latest graph file, returns null if no graph can be produced
     */
    public abstract File getGraph();

    /*
     * get latest est file, returns null if no est can be produced
     */
    public abstract File getEst();

    // public abstract File getGraphPath(String graph, String format);

    public abstract boolean isReady();

    public abstract void destroy();

    public abstract void clear();

    public abstract boolean isSemidelm();

    public abstract void setSemidelm(boolean semidelm);

    public void addStataListener(IStataListener listener);

    public void removeStataListener(IStataListener listener);

    public abstract String getWorkingdir();

}