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
        // 🔹 Construir vocabulario
        Set<String> allTerms = new HashSet<>();
        corpus.values().forEach(allTerms::addAll);
        vocab.addAll(allTerms);
        vocab.sort(String::compareTo);

        int N = corpus.size();

        // 🔹 Calcular IDF = log(N / df)
        for (String term : vocab) {
            int df = 0;
            for (List<String> docTokens : corpus.values()) {
                if (docTokens.contains(term)) df++;
            }
            idf.put(term, Math.log((double) N / (df + 1)));
        }

        // 🔹 Calcular TF y TF-IDF
        for (String doc : docNames) {
            List<String> tokens = corpus.get(doc);
            Map<String, Long> freq = tokens.stream()
                    .collect(Collectors.groupingBy(t -> t, Collectors.counting()));
            double total = tokens.size();

            Map<String, Double> tf = new LinkedHashMap<>();
            Map<String, Double> tfidf = new LinkedHashMap<>();

            for (String term : vocab) {
                double tfVal = freq.getOrDefault(term, 0L) / total;
                double tfidfVal = tfVal * idf.get(term);
                tf.put(term, tfVal);
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
