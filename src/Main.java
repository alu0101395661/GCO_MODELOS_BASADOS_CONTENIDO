import java.io.*;
import java.util.*;
import model.*;
import utils.FileUtils;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println("Uso: java Main <carpeta_corpus> <stopwords.txt> <lemmas.tsv> <carpeta_salida>");
            return;
        }

        String corpusDir = args[0];
        String stopPath = args[1];
        String lemmaPath = args[2];
        String outDir = args[3];

        Set<String> stopwords = FileUtils.loadStopwords(stopPath);
        Map<String, String> lemmas = FileUtils.loadLemmas(lemmaPath);

        DocumentProcessor dp = new DocumentProcessor(stopwords, lemmas);
        Map<String, List<String>> corpus = dp.loadAndProcessCorpus(corpusDir);

        TFIDF tfidf = new TFIDF(corpus);
        tfidf.compute();

        double[][] simMatrix = Similarity.computeCosineMatrix(tfidf.getVecNomVectors());
        FileUtils.saveDocumentTables(tfidf, outDir + "/per_document");
        FileUtils.saveSimilarityMatrix(tfidf.getDocNames(), simMatrix, outDir + "/similarity.csv");

        System.out.println("Proceso completado. Resultados en " + outDir);
    }
}
