package istata.domain;

public class EstBean {

    // hold a prefix
    private String prefix;
    
    // hold the command
    private String command;
    
    // hold the dependent(s)
    private String depvar;
    
    // hold the independents
    private String vars;
    
    // hold the restrictions (if, weight, etc)
    private String restrictions;
    
    // options
    private String options;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDepvar() {
        return depvar;
    }

    public void setDepvar(String depvar) {
        this.depvar = depvar;
    }

    public String getVars() {
        return vars;
    }

    public void setVars(String vars) {
        this.vars = vars;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
 
    
    
}
