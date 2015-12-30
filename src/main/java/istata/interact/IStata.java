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
     * get named graph file, returns null if no graph can be produced
     */
    public abstract File getGraph(String name);

    
    /*
     * get latest est file, returns null if no est can be produced
     */
    public abstract File getEst();

    public abstract boolean isReady();

    public abstract void destroy();

    public abstract void clear();

    public abstract boolean isSemidelm();

    public abstract void setSemidelm(boolean semidelm);

    public void addStataListener(IStataListener listener);

    public void removeStataListener(IStataListener listener);

    public abstract String getWorkingdir();

}