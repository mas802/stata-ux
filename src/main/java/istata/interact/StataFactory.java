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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class StataFactory {

    private final static String DEFAULT_KEY = "_DEFAULT";

    private static Map<String, IStata> instances = new HashMap<String, IStata>();

    public IStata getInstance() {
        return getInstance(DEFAULT_KEY);
    }

    public IStata getInstance(String key) {
        IStata instance = null;
        String initpath = (key.equals(DEFAULT_KEY)) ? null : key;
        if (!instances.containsKey(key)) {
            try {
                instance = new Stata(initpath);

                ((Stata) instance).init();
                instances.put(key, instance);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            instance = instances.get(key);
        }

        for (IStataListener l : listeners) {
            instance.addStataListener(l);
        }

        return instance;
    }

    public void dispose() {
        for (IStata s : instances.values()) {
            instances.remove(s);
            s.destroy();
        }
    }

    private Set<IStataListener> listeners = new HashSet<IStataListener>();

    public void addStataListener(IStataListener listener) {
        listeners.add(listener);
        for (IStata s : instances.values()) {
            s.addStataListener(listener);
        }
    }

    public void removeStataListener(IStataListener listener) {
        listeners.remove(listener);
        for (IStata s : instances.values()) {
            s.removeStataListener(listener);
        }
    }
}
