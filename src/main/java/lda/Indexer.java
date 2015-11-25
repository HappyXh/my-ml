package lda;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by happy on 11/1/15.
 */
public class Indexer {
    private int size;
    HashMap<String, Integer> token2idx = new HashMap<>();

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Indexer(String[] tokens){
        for(String token : tokens){
            token2idx.putIfAbsent(token, token2idx.size());
        }
        this.size=token2idx.size();
    }
    public Indexer(List< List<String> > dataSet){
        for(List<String> tokens : dataSet){
            for(String token : tokens) {
                token2idx.putIfAbsent(token, token2idx.size());
            }
        }
        this.size=token2idx.size();
    }

    public List<Integer> index(List<String> tokens){
        List<Integer> idxs= new ArrayList<>();
        for(String token : tokens){
            idxs.add(index(token));
        }
        return idxs;
    }

    public int index(String token){
        try {
            return token2idx.get(token);
        }catch (Exception e){
            return -1;
        }
    }

    public List<String> toArray(){
        List<String> tokens = new ArrayList<>();
        Iterator iter = token2idx.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = entry.getKey().toString();
            tokens.add(key);
        }
        return tokens;
    }

    public static int[][] min(int[][] a){
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
        System.out.println(b[0][0] + "    " + b[0][1]);
        return b;
    }
    public static int[][] getTop(int[] data,int top){
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
    public static void main(String args[]){
//        List<String> places = Arrays.asList("Buenos Aires", "Córdoba", "La Plata", "La Plata");
//        List<String> places2 = Arrays.asList("Buenos Aires", "Córdoba", "Plata");
//        Indexer indexer = new Indexer((String [])places.toArray());
//        System.out.println(indexer.index("Buenos Aires"));
//        System.out.println(indexer.index("Buenos"));
//        System.out.println(indexer.index(places));
//        System.out.println(indexer.index(places2));
//        System.out.println(indexer.toArray());
        int[][] a = new int[4][3];
        System.out.println(a.length);
//        a[0]=3;
//        a[1]=1;
//        a[2]=2;
//        a[3]=4;
//        int[][] b = getTop(a,3);
//        System.out.println(b[0][0]);
//        System.out.println(b[0][1]);
//        System.out.println(b[1][0]);
//        System.out.println(b[1][1]);
//        System.out.println(b[2][0]);
//        System.out.println(b[2][1]);
    }

}
