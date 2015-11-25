package lda;

import java.util.List;

/**
 * Created by happy on 11/2/15.
 */
public class Page {
    String content;
    String url;
    List<String> label_1;
    List<String> label_2;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getLabel_1() {
        return label_1;
    }

    public void setLabel_1(List<String> label_1) {
        this.label_1 = label_1;
    }

    public List<String> getLabel_2() {
        return label_2;
    }

    public void setLabel_2(List<String> label_2) {
        this.label_2 = label_2;
    }
}
