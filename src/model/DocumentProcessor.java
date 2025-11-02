package model;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class DocumentProcessor {

    private final Set<String> stopwords;
    private final Map<String, String> lemmas;
    private final Pattern tokenPattern = Pattern.compile("[A-Za-zÁÉÍÓÚÜÑáéíóúüñ’]+");

    public DocumentProcessor(Set<String> stopwords, Map<String, String> lemmas) {
        this.stopwords = stopwords;
        this.lemmas = lemmas;
    }

    /** Analizador de contenido: tokeniza, normaliza, elimina stopwords y lematiza */
    public Map<String, List<String>> loadAndProcessCorpus(String dir) throws IOException {
        Map<String, List<String>> corpus = new LinkedHashMap<>();
        Files.list(Paths.get(dir))
                .filter(p -> p.toString().endsWith(".txt"))
                .forEach(p -> {
                    try {
                        String name = p.getFileName().toString();
                        String text = Files.readString(p);
                        corpus.put(name, processText(text));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        return corpus;
    }

    private List<String> processText(String text) {
        List<String> tokens = new ArrayList<>();
        Matcher m = tokenPattern.matcher(text.toLowerCase());
        while (m.find()) {
            String word = m.group();
            
            if (!stopwords.contains(word)) {
                tokens.add(lemmas.getOrDefault(word, word));
            }
        }
        return tokens;
    }
}
