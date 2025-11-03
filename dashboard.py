import streamlit as st
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
from pathlib import Path

# --- CONFIG ---
st.set_page_config(page_title="Análisis TF-IDF y Similaridad", layout="wide")

# --- CARGA DE DATOS ---
output_path = Path("output")
per_doc_path = output_path / "per_document"
sim_path = output_path / "similarity.csv"

if not sim_path.exists():
    st.error("No se encontró 'output/similarity.csv'. Ejecuta primero el programa Java.")
    st.stop()

# Cargar matriz de similaridad
sim_df = pd.read_csv(sim_path, index_col=0)

# Cargar CSVs por documento
docs_data = {}
for csv in sorted(per_doc_path.glob("*.csv")):
    name = csv.stem
    try:
        df = pd.read_csv(csv)
        docs_data[name] = df
    except Exception as e:
        st.warning(f"No se pudo leer {csv.name}: {e}")

# --- UI ---
st.title(" Dashboard de Análisis TF-IDF y Similaridad")
st.write("Visualiza los resultados generados por el modelo basado en contenido.")

doc_names = list(docs_data.keys())
if not doc_names:
    st.error("No se encontraron CSVs en 'output/per_document/'.")
    st.stop()

selected_doc = st.selectbox("Selecciona un documento:", doc_names)

col1, col2 = st.columns([2, 1])

# --- TOP TF-IDF ---
with col1:
    st.subheader(f" Términos más relevantes en {selected_doc}")
    df = docs_data[selected_doc].sort_values(by="TF-IDF", ascending=False).head(15)
    fig, ax = plt.subplots(figsize=(8, 4))
    sns.barplot(data=df, x="TF-IDF", y="term", ax=ax, palette="viridis")
    st.pyplot(fig)

# --- SIMILARES ---
with col2:
    st.subheader(" Documentos más similares")
    similar_docs = sim_df[selected_doc].sort_values(ascending=False)
    st.dataframe(similar_docs[1:6].rename("Similaridad").to_frame())

# --- HEATMAP GLOBAL ---
st.subheader(" Matriz de similaridad coseno (todos los documentos)")
fig2, ax2 = plt.subplots(figsize=(8, 6))
sns.heatmap(sim_df, cmap="YlGnBu", annot=True, fmt=".2f", ax=ax2)
st.pyplot(fig2)
