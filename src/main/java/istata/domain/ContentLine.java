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
package istata.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ContentLine implements Comparable<ContentLine> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer line;
    private String content;

    public ContentLine() {
    }

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

    public String encContent() {
        try {
            return URLEncoder.encode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return content;
        }
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
        return (obj instanceof ContentLine) ? (this.getContent()
                .equals(((ContentLine) obj).getContent())) : super.equals(obj);
    }
}
