import streamlit as st
import pandas as pd
from datetime import datetime
import os

# ---------------- PATH SETUP ----------------
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA_DIR = os.path.join(BASE_DIR, "DATA")

# ---------------- STREAMLIT CONFIG ----------------
st.set_page_config(
    page_title="Mobile Forensic Dashboard",
    layout="wide"
)

st.title("📱 Mobile Forensic Investigation Dashboard")
st.caption("Visual Intelligence Layer for PS-6")

st.divider()

# ---------------- ACTIVITY BY HOUR ----------------
st.subheader("📊 Activity by Hour (Call Analysis)")

calls_path = os.path.join(DATA_DIR, "calls.csv")

if not os.path.exists(calls_path):
    st.error("❌ calls.csv file not found in DATA folder")
    st.stop()

# Load calls data
calls_df = pd.read_csv(calls_path)

# Validate required columns
required_columns = {"number", "time", "duration"}
if not required_columns.issubset(calls_df.columns):
    st.error("❌ calls.csv does not have required columns")
    st.stop()

# Extract hour from time
calls_df["hour"] = calls_df["time"].apply(
    lambda x: datetime.strptime(x, "%Y-%m-%d %H:%M").hour
)

# Group by hour
hourly_activity = (
    calls_df.groupby("hour")
    .size()
    .reset_index(name="call_count")
)

# Show table
with st.expander("🔍 View Hourly Call Data"):
    st.dataframe(hourly_activity, use_container_width=True)

# Plot chart
st.markdown("### 📈 Call Frequency by Hour")
st.line_chart(
    hourly_activity.rename(columns={"hour": "index"}).set_index("index")
)

# Late-night activity detection
late_night = hourly_activity[hourly_activity["hour"].between(0, 5)]

st.divider()

if not late_night.empty:
    st.warning(
        "⚠️ Suspicious late-night call activity detected between 00:00 and 05:00"
    )
else:
    st.success("✅ No suspicious late-night call activity detected")

# ---------------- FOOTER ----------------
st.divider()
st.caption(
    "Forensic Insight: Temporal behaviour analysis helps investigators "
    "identify abnormal usage patterns."
)




# =========================================================
# COMMUNICATION NETWORK (ADVANCED GRAPH)
# =========================================================

import networkx as nx
import matplotlib.pyplot as plt

st.divider()
st.subheader("🕸 Communication Network (Advanced Relationship Analysis)")

# Prepare graph
G = nx.Graph()

OWNER_NODE = "Owner"
G.add_node(OWNER_NODE)

# Count calls per contact
contact_counts = calls_df["number"].value_counts().to_dict()

for number, count in contact_counts.items():
    G.add_node(str(number))
    G.add_edge(OWNER_NODE, str(number), weight=count)

# Layout (force-directed)
pos = nx.spring_layout(G, k=0.6, seed=42)

# Node sizes (bigger = more important)
node_sizes = []
for node in G.nodes():
    if node == OWNER_NODE:
        node_sizes.append(2200)
    else:
        node_sizes.append(900 + contact_counts.get(int(node), 1) * 400)

# Node colors
node_colors = []
for node in G.nodes():
    if node == OWNER_NODE:
        node_colors.append("#FF6F61")  # Owner (red)
    else:
        node_colors.append("#4A90E2")  # Contacts (blue)

# Edge widths (stronger relationship = thicker line)
edge_widths = [G[u][v]["weight"] for u, v in G.edges()]

# Draw graph
fig, ax = plt.subplots(figsize=(8, 6))
nx.draw(
    G,
    pos,
    with_labels=True,
    node_size=node_sizes,
    node_color=node_colors,
    width=edge_widths,
    font_size=10,
    font_weight="bold",
    ax=ax
)

ax.set_title("Communication Relationship Graph", fontsize=14)
st.pyplot(fig)

# Insight text
most_contacted = max(contact_counts, key=contact_counts.get)

st.info(
    f"📌 Most contacted number: {most_contacted} "
    f"({contact_counts[most_contacted]} interactions)"
)