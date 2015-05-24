package istata.interact.model;

public class StataVar {

    private String name;
    private String type;
    private String format;
    private String valueLabel;
    private boolean hasNotes;
    private String label;

    public StataVar(String describe) {
        // System.out.println(describe);
        this.name = describe.replaceFirst("(^\\S*?)\\s(.*?)$", "$1");
        // System.out.println(name);
        describe = describe.substring(this.name.length()).trim();
        int len = describe.length();
        int start = 0;
        int end = 7;
        this.type = describe.substring(0, 7).trim();
        start = end;
        end = Math.min(19, len);
        this.format = describe.substring(start, end).trim();
        start = end;
        end = Math.min(27, len);
        this.valueLabel = describe.substring(start, end).trim();
        start = end;
        end = Math.min(30, len);
        this.hasNotes = (describe.substring(start, end).trim().equals(". *"));
        start = end;
        this.label = describe.substring(start);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getValueLabel() {
        return valueLabel;
    }

    public void setValueLabel(String valueLabel) {
        this.valueLabel = valueLabel;
    }

    public boolean isHasNotes() {
        return hasNotes;
    }

    public void setHasNotes(boolean hasNotes) {
        this.hasNotes = hasNotes;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return name + "|\t|" + type + "|\t|" + format + "|\t|" + valueLabel
                + "|\t|" + hasNotes + "|\t|" + label;
    }
}
