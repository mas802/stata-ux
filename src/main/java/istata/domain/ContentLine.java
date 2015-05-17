package istata.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ContentLine implements Comparable<ContentLine>{

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer line;
    private String content;
    
    public ContentLine() {}
    
    public ContentLine(int line, String content) {
        this.line = line;
        this.content = content;
    }
    
    
    public Integer getLine() {
        return line;
    }
    public void setLine(Integer line) {
        this.line = line;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public int compareTo(ContentLine o) {
        return this.getContent().compareTo(o.getContent());
    }
    
    @Override
    public int hashCode() {
        return getContent().toString().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ContentLine)?(this.getContent()
                .equals(((ContentLine)obj).getContent())):super.equals(obj);
    }
}
