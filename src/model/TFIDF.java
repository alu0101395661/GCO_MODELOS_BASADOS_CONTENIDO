package model;

import java.util.*;

public class TFIDF {
    private final Map<String, List<String>> corpus;
    private final List<String> docNames;
    private final List<String> vocab;
    private final Map<String, Double> idf;
    private final Map<String, Map<String, Integer>> tfVectors;
    private final Map<String, Map<String, Double>> tfPondVectors;
    private final Map<String, Map<String, Double>> tfidfVectors;
    private final Map<String, Map<String, Double>> vecNomVectors;

    public TFIDF(Map<String, List<String>> corpus) {
        this.corpus = corpus;
        this.docNames = new ArrayList<>(corpus.keySet());
        this.vocab = new ArrayList<>();
        this.idf = new HashMap<>();
        this.tfVectors = new LinkedHashMap<>();
        this.tfPondVectors = new LinkedHashMap<>();
        this.tfidfVectors = new LinkedHashMap<>();
        this.vecNomVectors = new LinkedHashMap<>();
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
            Map<String, Integer> freq = new HashMap<>();
            for (String term : vocab ) {
                int tf = 0;
                for (String s : tokens) {
                    if (s.equals(term)) {
                        tf++;
                    }
                }

                if (tf == 0) { continue; }

                freq.put(term, tf);
            }

            tfVectors.put(doc, freq);

            Map<String, Double> tf = new LinkedHashMap<>();
            Map<String, Double> vecNom = new LinkedHashMap<>();
            Map<String, Double> tfidf = new LinkedHashMap<>();

            List<Double> tfValues = new ArrayList<>();

            for (String term : vocab) {
                if (term.equals("a") || term.equals("i")) { continue; }
                double tfVal = Math.log10((double) freq.getOrDefault(term, 1)) + 1;
                double tfidfVal = tfVal * idf.get(term);
                if (!freq.containsKey(term)) { tfVal = 0; }
                tfValues.add(tfVal);
                tf.put(term, tfVal);
                tfidf.put(term, tfidfVal);
            }

            double sumCuad = 0;

            for (double v : tfValues) {
                sumCuad += v * v;
            }

            sumCuad = Math.sqrt(sumCuad);
            sumVec.put(doc, sumCuad);

            for (String term : vocab) {
                if (term.equals("a") || term.equals("i")) { continue; }
                double vecNomVal = tf.get(term) / sumVec.get(doc);
                vecNom.put(term, vecNomVal);
            }

            tfPondVectors.put(doc, tf);
            tfidfVectors.put(doc, tfidf);
            vecNomVectors.put(doc, vecNom);
        }
    }

    public List<String> getDocNames() { return docNames; }
    public List<String> getVocab() { return vocab; }
    public Map<String, Double> getIdf() { return idf; }
    public Map<String, Map<String, Integer>> getTfVectors() { return tfVectors; }
    public Map<String, Map<String, Double>> getTfPondVectors() { return tfPondVectors; }
    public Map<String, Map<String, Double>> getTfidfVectors() { return tfidfVectors; }
    public Map<String, Map<String, Double>> getVecNomVectors() { return vecNomVectors; }
}
