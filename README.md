# Sistema de Recomendación – Modelo Basado en el Contenido

---

##  Descripción del proyecto

Este proyecto implementa un **sistema de recomendación basado en el contenido**, siguiendo el enfoque **TF–IDF**.  
El programa analiza un conjunto de documentos de texto y calcula su **similaridad coseno** para identificar cuáles son más parecidos entre sí.

Cuenta con un  **dashboard interactivo** con *Streamlit* para **visualizar los resultados** generados por el modelo.


---

##  Requisitos

- **Java**
- **Python** con las siguientes librerías:
  ```bash
  pip install streamlit pandas seaborn matplotlib
  ```

---

##  Ejecución

1. **Compilar** el código (desde la raíz del proyecto):

   ```bash
   javac -d bin src/Main.java src/model/*.java src/utils/*.java
   ```

2. **Ejecutar** el programa pasando los 4 argumentos requeridos:

   ```bash
   java -cp bin Main data word-utils/stop-words-es.txt word-utils/corpus-es.json output
   ```

3. El programa generará los resultados en `output/`, con:
   - `per_document/` → CSV por documento con las columnas:  
     `index, term, TF, IDF, TF-IDF`
   - `similarity.csv` → matriz de similaridad coseno entre documentos.

---

## Ejemplo de salida

**Per-documento (`output/per_document/doc1.csv`):**
```
index,term,TF,IDF,TF-IDF
1,informacion,0.25,1.39,0.35
2,datos,0.25,1.10,0.28
...
```

**Matriz de similaridad (`output/similarity.csv`):**
```
,doc1,doc2,doc3
doc1,1.0000,0.8411,0.6503
doc2,0.8411,1.0000,0.7021
doc3,0.6503,0.7021,1.0000
```

---

##  Lógica

1. **Tokenización y filtrado:**  
   Se eliminan signos, números y *stopwords*.
2. **Lematización:**  
   Se normalizan las palabras usando el fichero `corpus-es.json`.
3. **Cálculo de TF-IDF:**  
   Se obtiene el peso de cada término en cada documento.
4. **Similitud coseno:**  
   Se mide la similitud entre todos los pares de documentos mediante el producto escalar normalizado.

---

## Dashboard para visualizar los resultados

Aplicación desarrollada con **Python y Streamlit** que permite analizar los resultados de forma visual.

### Ejecución
Desde la raíz del proyecto, tras generar los CSVs:

```bash
streamlit run dashboard.py
```

Abrirá un panel local en:  
[http://localhost:8501](http://localhost:8501)

Donde se puede:

- Seleccionar de un documento para ver sus términos más relevantes.
- Visualizar los **Top-N términos por TF-IDF**.
- Tabla con los **documentos más similares**.
- **Heatmap** de la matriz de similaridad.

---

