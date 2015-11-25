package lda;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by happy on 11/2/15.
 */
public class GS0LabeledLDA {
    int numTopics;
    int numTerms;
    int numDocs;
    Double alpha;
    Double beta = 0.01;
    Indexer termIndex;
    Indexer labelIndex;
    List<Doc> trainSet;
    int[] countTopic;
    int[][] countTopicTerm;
    int[][] countDocTopic;
    List<List<Integer>> topicDocTerm;

    public GS0LabeledLDA(int numTopics, int numTerms, Indexer termIndex,
                         Indexer labelIndex, List<Doc> dataSet){
        this.alpha = 50*1.0 / numTopics;
        this.numDocs = dataSet.size();
        this.numTopics = numTopics;
        this.numTerms = numTerms;
        this.termIndex = termIndex;
        this.labelIndex = labelIndex;
        this.trainSet = dataSet;
        this.countTopic = new int[numTopics];
        this.countTopicTerm =new int[numTopics][numTerms];


        init(dataSet,true);

    }
    public void init(List<Doc> dataSet, Boolean learn){
        int numSet = dataSet.size();
        this.topicDocTerm = new ArrayList<>();
        this.countDocTopic = new int[dataSet.size()][numTopics];
        Random random = new Random(12);
        int r, k;
        for(int i = 0; i<numSet; i++){
            Doc doc = dataSet.get(i);
            int lengthDoc = doc.getPages().size();
            List<Integer> topics= new ArrayList<>();
            for(int j = 0; j<lengthDoc; j++){
                String term = doc.getPages().get(j);
                if(learn) {
                    r = random.nextInt(doc.getLabels().size());
                    k = labelIndex.index(doc.getLabels().get(r));
                    countDocTopic[i][k] += 1;
                    countTopic[k] += 1;
                    countTopicTerm[k][termIndex.index(term)] += 1;
                }else{
                    k =random.nextInt(numTopics);
                    countDocTopic[i][k] += 1;
                }
                topics.add(k);

            }
            topicDocTerm.add(topics);
        }
    }

    public void train(int maxIte){
        Boolean flag = true;
        int iter = 0;
        while(flag){
            iter += 1;
            System.out.println(iter);
            doAssignment();
            if(iter > maxIte) flag = false;
        }
    }
    public void infer(List<Doc> testSet,Double delta){
        init(testSet,false);
        //此处需要深拷贝
        Double gap = 0.0;
        List<List<Integer>> last = topicDocTerm;
        do{
            testAssignment(testSet);
            gap = gap();
        }while(gap > delta);


    }

    private Double gap() {
        return 0.0;
    }

    public void testAssignment(List<Doc> testSet){
        int numSet = testSet.size();
        for (int i = 0; i<numSet; i++) {
            List<String> doc = testSet.get(i).getPages();
            List<Integer> topics = topicDocTerm.get(i);
            int lengthDoc = doc.size();
            for(int j = 0; j<lengthDoc; j++){
                String term = doc.get(j);
                int k = topics.get(j);
                countDocTopic[i][k] -= 1;
                k = gsTopic(i, termIndex.index(term));
                countDocTopic[i][k] += 1;
                //may have problem
                topics.set(j, k);
            }
        }
    }
    public void doAssignment(){
        for (int i = 0; i<numDocs; i++) {
            List<String> doc = trainSet.get(i).getPages();
            List<Integer> topics = topicDocTerm.get(i);
            int lengthDoc = doc.size();
            for(int j = 0; j<lengthDoc; j++){
                String term = doc.get(j);
                int k = topics.get(j);
                countDocTopic[i][k] -= 1;
                countTopic[k] -= 1;
                countTopicTerm[k][termIndex.index(term)] -= 1;

                k = gsTopic(i, termIndex.index(term));
                countDocTopic[i][k] += 1;
                countTopic[k] += 1;
                countTopicTerm[k][termIndex.index(term)] += 1;
                //may have problem
                topics.set(j, k);

            }
        }
    }
    public int gsTopic(int m, int t){
        Random random = new Random();
        Double[] prob = new Double[numTopics];
        Double sumProb = 0.0;
        for(int k = 0; k<numTopics; k++){
            prob[k]= getProb(m, t, k);
            sumProb += prob[k];
        }

        Double p = random.nextFloat()*sumProb;
        for(int k = 0; k<numTopics; k++){
            if(prob[k] < p){
                p -= prob[k];
            }else{
                return k;
            }
        }
        return numTopics - 1;
    }
    public Double getProb(int m, int t, int k){
        return (countTopicTerm[k][t] + this.beta)*1.0 / (countTopic[k] + this.beta*numTerms)
                * (countDocTopic[m][k] + this.alpha);
    }
    public void evaluation(){

    }
    public void summary(String filePath) throws IOException {
        File file = new File(filePath);
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(filePath,true);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        int[][] a;
        for(int i = 0; i<numTopics; i++){
            bw.newLine();
            bw.write("Topic " + i + ": " + labelIndex.toArray().get(i) + " top terms:");
            a = getTop(countTopicTerm[i],15);
            for(int j = 0; j<a.length; j++) {
                bw.newLine();
                bw.write(termIndex.toArray().get(a[j][0]) + "  " + a[j][1]);
            }
        }
        fileWriter.flush();
        bw.close();
        fileWriter.close();
    }
    public int[][] getTop(int[] data,int top){
        int[][] a = new int[top][2];
        for(int i = 0; i<a.length; i++){
            a[i][0] = i;
        }
        int[][] b;
        b =min(a);
        int m = data.length;
        for(int i = 0; i<m; i++){
            if(data[i] > b[0][1]){
                a[b[0][0]][0] = i;
                a[b[0][0]][1] = data[i];
                b =min(a);
            }
        }
        return a;
    }
    public int[][] min(int[][] a){
        int[][] b =new int[1][2];
        b[0][0] = 0;
        b[0][1] = a[0][1];
        int m = a.length;
        for(int i =0; i<m; i++){
            if(a[i][1] < b[0][1]){
                b[0][1] = a[i][1];
                b[0][0] = i;
            }
        }
        return b;
    }

}
