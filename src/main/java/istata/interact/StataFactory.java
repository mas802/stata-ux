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
        
        for ( IStataListener l: listeners) {
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
