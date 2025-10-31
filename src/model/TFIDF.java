package model;

import java.util.*;
import java.util.stream.Collectors;

public class TFIDF {
    private final Map<String, List<String>> corpus;
    private final List<String> docNames;
    private final List<String> vocab;
    private final Map<String, Double> idf;
    private final Map<String, Map<String, Double>> tfVectors;
    private final Map<String, Map<String, Double>> tfidfVectors;

    public TFIDF(Map<String, List<String>> corpus) {
        this.corpus = corpus;
        this.docNames = new ArrayList<>(corpus.keySet());
        this.vocab = new ArrayList<>();
        this.idf = new HashMap<>();
        this.tfVectors = new LinkedHashMap<>();
        this.tfidfVectors = new LinkedHashMap<>();
    }

    public void compute() {
        //  Construir vocabulario
        Set<String> allTerms = new HashSet<>();
        corpus.values().forEach(allTerms::addAll);
        vocab.addAll(allTerms);
        vocab.sort(String::compareTo);

        int N = 0;
        for (List<String> f : corpus.values()) {
            N += f.size();
        }
        

        // Calcular IDF = log(N / df)
        for (String term : vocab) {
            int df = 0;
            for (List<String> docTokens : corpus.values()) {
                for (String s : docTokens) {
                    if (s.equals(term)) { df++; }
                }
            }
            idf.put(term, Math.log((double) N / df));
        }

        Map<String, Double> sumVec = new HashMap<>();

        //  Calcular TF y TF-IDF
        for (String doc : docNames) {
            List<String> tokens = corpus.get(doc);
            Map<String, Long> freq = tokens.stream().collect(Collectors.groupingBy(t -> t, Collectors.counting()));

            Map<String, Double> tf = new LinkedHashMap<>();
            Map<String, Double> tfidf = new LinkedHashMap<>();

            List<Double> tfValues = new ArrayList<>();

            for (String term : vocab) {
                double tfVal = Math.log10((double) freq.getOrDefault(term, 1L)) + 1;
                if (tfVal == 1) { tfVal = 0; }
                tfValues.add(tfVal);
                tf.put(term, tfVal);
            }

            double sumCuad = 0;

            for (double v : tfValues) {
                sumCuad += v * v;
            }

            sumCuad = Math.sqrt(sumCuad);
            sumVec.put(doc, sumCuad);

            for (String term : vocab) {
                double tfidfVal = tf.get(term) / sumVec.get(doc);
                tfidf.put(term, tfidfVal);
            }

            tfVectors.put(doc, tf);
            tfidfVectors.put(doc, tfidf);
        }
    }

    public List<String> getDocNames() { return docNames; }
    public List<String> getVocab() { return vocab; }
    public Map<String, Double> getIdf() { return idf; }
    public Map<String, Map<String, Double>> getTfVectors() { return tfVectors; }
    public Map<String, Map<String, Double>> getTfidfVectors() { return tfidfVectors; }
}
