package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import model.TFIDF;

/** Utilidades de E/S para la práctica de TF-IDF */
public class FileUtils {

    /** Carga stopwords: ignora líneas vacías y comentarios que empiecen por # */
    public static Set<String> loadStopwords(String path) throws IOException {
        Set<String> stops = new HashSet<>();
        try (BufferedReader br = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                stops.add(line.toLowerCase());
            }
        }
        return stops;
    }

    /**
     * Carga lemas desde un TSV: "forma<TAB>lema".
     * Acepta también separación por espacios múltiples por si el fichero no tiene tabs estrictos.
     * Ignora líneas vacías y comentarios que empiecen por #.
     */
    public static Map<String, String> loadLemmas(String path) throws IOException {
        String text = Files.readString(Path.of(path));
        text = text.trim()
                .replaceAll("[{}\"]", ""); // elimina llaves y comillas
        Map<String, String> map = new LinkedHashMap<>();
        for (String pair : text.split(",")) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                map.put(kv[0].trim(), kv[1].trim());
            }
        }
        return map;
    }

    /** Guarda un CSV por documento con: índice, término, TF, IDF, TF-IDF */
    public static void saveDocumentTables(TFIDF tfidf, String outDir) throws IOException {
        Files.createDirectories(Path.of(outDir));

        List<String> vocab = tfidf.getVocab();
        Map<String, Double> idfMap = tfidf.getIdf();

        for (String doc : tfidf.getDocNames()) {
            Map<String, Integer> tfMap = tfidf.getTfVectors().get(doc);
            Map<String, Double> tfPondMap = tfidf.getTfPondVectors().get(doc);
            Map<String, Double> tfidfMap = tfidf.getTfidfVectors().get(doc);

            try (PrintWriter pw = new PrintWriter(Path.of(outDir, doc + ".csv").toString(), StandardCharsets.UTF_8)) {
                pw.println("index,term,TF(log),TF,IDF,TF-IDF");
                int index = 0;
                for (int i = 0; i < vocab.size(); i++) {
                    String term = vocab.get(i);
                    if (term.equals("a") || term.equals("i")) { continue; }
                    if (tfMap.getOrDefault(term, 0) == 0) continue;
                    long tf = tfMap.get(term);
                    double tfPond = tfPondMap.get(term);
                    double idf = idfMap.get(term);
                    double tfidfVal = tfidfMap.get(term);
                    pw.printf(Locale.US, "%d,%s,%.6f,%d,%.6f,%.6f%n", index, term, tfPond, tf, idf, tfidfVal);
                    index++;
                }
            }
        }
    }

    /** Guarda la matriz de similitud coseno en CSV */
    public static void saveSimilarityMatrix(List<String> names, double[][] M, String path) throws IOException {
        try (PrintWriter pw = new PrintWriter(path, StandardCharsets.UTF_8)) {
            pw.print(",");
            pw.println(String.join(",", names));
            for (int i = 0; i < names.size(); i++) {
                pw.print(names.get(i));
                for (int j = 0; j < names.size(); j++) {
                    pw.printf(Locale.US, ",%.4f", M[i][j]);
                }
                pw.println();
            }
        }
    }
}

