package lda;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.util.ArrayBuilders;

import javax.xml.crypto.Data;

/**
 * Created by happy on 11/2/15.
 */
public class BaseStoneMark {
    public List<String> stopWords;

    public BaseStoneMark() throws IOException {
        stopWords = new ArrayList<>();
        String tempString;
        File file = new File("src/main/resources/stopwords.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while((tempString = reader.readLine()) != null){
            stopWords.add(tempString);
        }
    }

    public List<String> token(String text){

        List<Term> parse = ToAnalysis.parse(text);
        List<String> keys = new ArrayList<>();
        for (Term term : parse) {
            String t = term.getName();
            String ns = term.getNatrue().natureStr;
            if(t != "nbsp" && t.length() >= 2 && !stopWords.contains(t)){
                keys.add(t);
            }
        }
        return keys;
    }
    public static void  main(String args[]) throws IOException {
        long t1 = System.currentTimeMillis();
        BaseStoneMark myTest = new BaseStoneMark();
        List<Doc> trainSet = new ArrayList<>();
        List<Doc> testSet = new ArrayList<>();

        String dataSource = "src/main/resources/lda_trainData.txt";
        File file = new File(dataSource);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String tempString = null;
        Random rnd = new Random();
        while((tempString = reader.readLine()) != null){
            Page page = new ObjectMapper().readValue(tempString, Page.class);
            List<String> tokens = myTest.token(page.content);
            List<String> labels = page.getLabel_2();
            if(labels.size()  == 1) {
                if(rnd.nextInt(10) >= 1){
                    trainSet.add(new Doc(tokens, labels));
                }else{
                    testSet.add(new Doc(tokens, labels));
                }
            }
        }
        List<List<String>> terms= new ArrayList<>();
        List<List<String>> labels= new ArrayList<>();
        for(Doc doc : trainSet){
            terms.add(doc.getPages());
            labels.add(doc.getLabels());
        }
        Indexer termIndex = new Indexer(terms);
        Indexer labelIndex = new Indexer(labels);
        int numTopics = labelIndex.getSize();
        int numTerms = termIndex.getSize();

        long t2 = System.currentTimeMillis();
        System.out.printf("Done! <load data> : %fs%n", (t2 - t1) / 1000.0);
        System.out.println("length:" + "\t" + trainSet.size() + "\t" +
                testSet.size());
        t1 = t2;

        GS0LabeledLDA labeledLda = new GS0LabeledLDA(numTopics,numTerms,termIndex,labelIndex,trainSet);
        labeledLda.train(100);

        t2 = System.currentTimeMillis();
        System.out.printf("Done! <train data> : %fs%n", (t2 - t1) / 1000.0);

        labeledLda.summary("src/main/resources/label_summary.txt");



    }
}

class Doc {
    private List<String> pages = new ArrayList<>();
    private List<String> labels = new ArrayList<>();

    public Doc(List<String> pages, List<String> labels){
        this.pages = pages;
        this.labels = labels;
    }

    public List<String> getPages() {
        return pages;
    }

    public void setPages(List<String> pages) {
        this.pages = pages;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

}