import streamlit as st
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
from pathlib import Path

# CONFIGURACIÓN DE LA PÁGINA
st.set_page_config(page_title="Panel de Análisis TF-IDF y Similaridad", layout="wide")

# SELECCIÓN DEL CONJUNTO DE DATOS
st.sidebar.title("Configuración")

dataset = st.sidebar.radio(
    "Selecciona el conjunto de datos:",
    ("output (corpus original)", "output2 (corpus extra)")
)

if "output2" in dataset:
    output_path = Path("output2")
else:
    output_path = Path("output")

per_doc_path = output_path / "per_document"
sim_path = output_path / "similarity.csv"

# CARGA DE DATOS
if not sim_path.exists():
    st.error(f"No se encontró el archivo {sim_path}. Ejecuta primero el programa Java para generar los CSV.")
    st.stop()

sim_df = pd.read_csv(sim_path, index_col=0)

docs_data = {}
for csv in sorted(per_doc_path.glob("*.csv")):
    try:
        df = pd.read_csv(csv)
        docs_data[csv.stem] = df
    except Exception as e:
        st.warning(f"No se pudo leer el archivo {csv.name}: {e}")

if not docs_data:
    st.error("No se encontraron documentos CSV en la carpeta seleccionada.")
    st.stop()

# TÍTULO
st.title(f"Panel de Análisis TF-IDF y Similaridad — {'Corpus Extra' if 'output2' in dataset else 'Corpus original'}")
st.markdown("Visualización de los resultados generados por el modelo basado en el contenido (TF-IDF y Similaridad del coseno).")

# SELECCIÓN DE DOCUMENTO
doc_names = list(docs_data.keys())
selected_doc = st.selectbox("Selecciona un documento:", doc_names)

col1, col2 = st.columns([2, 1])

# TÉRMINOS CON MAYOR PESO TF-IDF
with col1:
    st.subheader(f"Términos más relevantes en {selected_doc}")
    df = docs_data[selected_doc].sort_values(by="TF-IDF", ascending=False).head(20)
    fig, ax = plt.subplots(figsize=(8, 4))
    sns.barplot(data=df, x="TF-IDF", y="term", ax=ax, palette="viridis")
    ax.set_xlabel("Peso TF-IDF")
    ax.set_ylabel("Término")
    st.pyplot(fig)

# DOCUMENTOS MÁS SIMILARES
with col2:
    st.subheader("Documentos más similares")
    similar_docs = sim_df[selected_doc].sort_values(ascending=False)
    st.dataframe(similar_docs[1:6].rename("Similaridad").to_frame())

    # Botón para exportar ranking de similitud
    csv_bytes = similar_docs.rename("Similaridad").to_csv().encode("utf-8")
    st.download_button(
        label="Descargar similitudes como CSV",
        data=csv_bytes,
        file_name=f"{selected_doc}_similaridades.csv",
        mime="text/csv"
    )

# MATRIZ DE SIMILARIDAD GLOBAL
st.subheader("Matriz de Similaridad del Coseno")
fig2, ax2 = plt.subplots(figsize=(8, 6))
sns.heatmap(sim_df, cmap="YlGnBu", annot=True, fmt=".2f", ax=ax2)
ax2.set_xlabel("Documento")
ax2.set_ylabel("Documento")
st.pyplot(fig2)

# COMPARADOR DIRECTO ENTRE DOCUMENTOS
st.markdown("---")
st.subheader("Comparador directo de documentos")

doc_a = st.selectbox("Documento A", doc_names, key="doc_a")
doc_b = st.selectbox("Documento B", doc_names, key="doc_b")

if doc_a != doc_b:
    similarity_value = sim_df.loc[doc_a, doc_b]
    st.write(f"Similaridad del coseno entre {doc_a} y {doc_b}: {similarity_value:.3f}")

    # Términos comunes
    terms_a = set(docs_data[doc_a]['term'])
    terms_b = set(docs_data[doc_b]['term'])
    common_terms = terms_a & terms_b
    st.write(f"Términos comunes ({len(common_terms)}):")
    if common_terms:
        st.text(", ".join(list(common_terms)[:25]) + ("..." if len(common_terms) > 25 else ""))
    else:
        st.write("No se encontraron términos comunes.")
