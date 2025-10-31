package model;

import java.util.*;

public class Similarity {

    /** Calcula la similitud coseno entre dos vectores (pág. 138 del documento) */
    public static double cosine(Map<String, Double> a, Map<String, Double> b) {
        double dot = 0;
        for (String term : a.keySet()) {
            double va = a.get(term);
            double vb = b.get(term);
            dot += va * vb;
        }
        return dot;
    }

    public static double[][] computeCosineMatrix(Map<String, Map<String, Double>> vectors) {
        List<String> docs = new ArrayList<>(vectors.keySet());
        int n = docs.size();
        double[][] M = new double[n][n];

        for (int i = 0; i < n; i++) {
            M[i][i] = 1.0;
            for (int j = i + 1; j < n; j++) {
                double sim = cosine(vectors.get(docs.get(i)), vectors.get(docs.get(j)));
                M[i][j] = M[j][i] = sim;
            }
        }
        return M;
    }
}
