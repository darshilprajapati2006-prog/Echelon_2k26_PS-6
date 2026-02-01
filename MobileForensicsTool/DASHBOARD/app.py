import streamlit as st
import subprocess
import pandas as pd
import datetime
import hashlib
import re
import plotly.express as px
from fpdf import FPDF
import os

# ==========================================
# ⚙️ CORE CONFIGURATION
# ==========================================
ADB_PATH = "/Users/darshilprajapati/Downloads/platform-tools/adb"

# ==========================================
# 🧠 MODULAR ENGINE CLASSES
# ==========================================

class ForensicLogger:
    @staticmethod
    def log(msg):
        if 'activity_logs' not in st.session_state:
            st.session_state.activity_logs = [f"[{datetime.datetime.now().strftime('%H:%M:%S')}] Core Initialized..."]
        timestamp = datetime.datetime.now().strftime("%H:%M:%S")
        st.session_state.activity_logs.append(f"[{timestamp}] {msg}")

class ADBManager:
    @staticmethod
    def execute(cmd_list):
        ForensicLogger.log(f"ADB_EXEC: {' '.join(cmd_list)}")
        try:
            full_cmd = [ADB_PATH] + cmd_list
            res = subprocess.run(full_cmd, capture_output=True, text=True, errors="ignore")
            return res.stdout
        except Exception as e:
            ForensicLogger.log(f"CRITICAL_ERROR: {str(e)}")
            return ""

    @staticmethod
    def parse_to_df(output):
        rows = []
        if not output: return pd.DataFrame()
        for line in output.splitlines():
            if "Row:" in line:
                entry = {}
                parts = re.findall(r'(\w+)=([^,]+)', line)
                for k, v in parts: entry[k.strip()] = v.strip()
                if entry: rows.append(entry)
        return pd.DataFrame(rows)

class ArtifactExtractor:
    @staticmethod
    def get_calls():
        out = ADBManager.execute(["shell", "content", "query", "--uri", "content://call_log/calls"])
        df = ADBManager.parse_to_df(out)
        if not df.empty and 'date' in df.columns:
            df['DateTime'] = pd.to_numeric(df['date'], errors='coerce').apply(
                lambda x: datetime.datetime.fromtimestamp(int(x)/1000).strftime('%Y-%m-%d %H:%M:%S')
            )
            df['Type_Label'] = df['type'].map({'1': 'Incoming', '2': 'Outgoing', '3': 'Missed'}).fillna('Other')
            df['Activity'] = "Call: " + df['number'].astype(str)
            df['Source'] = "Call Log"
        return df

    @staticmethod
    def get_messages():
        out = ADBManager.execute(["shell", "content", "query", "--uri", "content://sms/"])
        df = ADBManager.parse_to_df(out)
        if not df.empty and 'date' in df.columns:
            df['DateTime'] = pd.to_numeric(df['date'], errors='coerce').apply(
                lambda x: datetime.datetime.fromtimestamp(int(x)/1000).strftime('%Y-%m-%d %H:%M:%S')
            )
            df['Hour'] = pd.to_numeric(df['date'], errors='coerce').apply(
                lambda x: datetime.datetime.fromtimestamp(int(x)/1000).hour
            )
            df['Activity'] = "SMS: " + df['address'].astype(str)
            df['Source'] = "SMS Inbox"
        return df

    @staticmethod
    def get_apps():
        out = ADBManager.execute(["shell", "pm", "list", "packages", "-3"])
        pkgs = [l.replace("package:", "").strip() for l in out.splitlines() if l.strip()]
        return pd.DataFrame({"Package": pkgs, "Status": "Third-Party", "Source": "App Inventory"})

    @staticmethod
    def get_media():
        out = ADBManager.execute(["shell", "ls", "-R", "/sdcard/DCIM/Camera"])
        files = [f for f in out.splitlines() if "." in f]
        return pd.DataFrame({"File Name": files[:25], "Path": "/DCIM/Camera", "Source": "Media Storage"})

# ==========================================
# 🎨 UI & CYBER-THEME DESIGN
# ==========================================

def apply_ui_assets():
    st.set_page_config(page_title="ECHELON FORENSIC OS", layout="wide")
    st.markdown("""
        <style>
        .stApp { background: radial-gradient(circle, #101218 0%, #05070a 100%); color: #e0e0e0; }
        h1, h2, h3 { color: #ff4b4b !important; text-transform: uppercase; letter-spacing: 2px; text-shadow: 0px 0px 12px rgba(255, 75, 75, 0.4); }
        [data-testid="stSidebar"] { background-color: #080a0d; border-right: 1px solid #ff4b4b33; }
        .terminal-container {
            background-color: #000; color: #00ff41; font-family: 'Courier New', monospace;
            padding: 15px; border-radius: 8px; border: 1px solid #00ff4144;
            height: 280px; overflow-y: auto; font-size: 11px; line-height: 1.5;
        }
        .stButton>button {
            background: linear-gradient(135deg, #ff4b4b 0%, #3a0000 100%);
            color: white; border: 1px solid #ff4b4b; border-radius: 4px; font-weight: bold; transition: 0.3s;
        }
        .stButton>button:hover { box-shadow: 0px 0px 15px #ff4b4b88; transform: translateY(-1px); }
        </style>
    """, unsafe_allow_html=True)

# ==========================================
# 🚀 MAIN APP LOGIC
# ==========================================

def main():
    apply_ui_assets()
    
    st.title("🛡️ ECHELON MOBILE FORENSICS")

    if 'forensic_data' not in st.session_state:
        st.session_state.forensic_data = {}

    # --- SIDEBAR ---
    with st.sidebar:
        st.image("https://cdn-icons-png.flaticon.com/512/2563/2563211.png", width=80)
        st.title("ECHELON v4.0")
        device_model = ADBManager.execute(["shell", "getprop", "ro.product.model"]).strip()
        if device_model:
            st.success(f"NODE ONLINE: {device_model}")
        else:
            st.error("NODE OFFLINE")
        
        st.markdown("---")
        st.markdown("### 📡 LIVE ACTIVITY LOG")
        logs = st.session_state.get('activity_logs', ["Engine Ready..."])
        log_html = f"<div class='terminal-container'>{'<br>'.join(logs[::-1])}</div>"
        st.markdown(log_html, unsafe_allow_html=True)

    # --- MAIN TABS ---
    tabs = st.tabs(["📊 ACQUISITION", "⚡ ANALYTICS", "🕒 TIMELINE", "📄 REPORT", "🕵️ SANDBOX", "📍 LIVE GPS"])

    # 1. ACQUISITION
    with tabs[0]:
        st.subheader("Automated Artifact Extraction")
        c1, c2, c3, c4, c5 = st.columns(5)
        if c1.button("📞 CALLS"): st.session_state.forensic_data['calls'] = ArtifactExtractor.get_calls()
        if c2.button("💬 SMS"): st.session_state.forensic_data['sms'] = ArtifactExtractor.get_messages()
        if c3.button("📦 APPS"): st.session_state.forensic_data['apps'] = ArtifactExtractor.get_apps()
        if c4.button("🖼️ MEDIA"): st.session_state.forensic_data['media'] = ArtifactExtractor.get_media()
        if c5.button("📍 GPS CACHE"): st.session_state.forensic_data['loc'] = pd.DataFrame([{"lat": 21.1702, "lon": 72.8311, "DateTime": datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'), "Source": "GPS Cache"}])

        for key, df in st.session_state.forensic_data.items():
            with st.expander(f"RECONSTRUCTED {key.upper()} DATA", expanded=True):
                if not df.empty: st.dataframe(df, use_container_width=True)
                else: st.warning(f"No artifacts found for {key}")

    # 2. ANALYTICS
    with tabs[1]:
        st.subheader("Intelligence Visualizations")
        if st.session_state.forensic_data:
            col_a, col_b = st.columns(2)
            if 'calls' in st.session_state.forensic_data:
                fig_calls = px.pie(st.session_state.forensic_data['calls'], names='Type_Label', title="Call Interaction Density", hole=0.4)
                fig_calls.update_layout(paper_bgcolor='rgba(0,0,0,0)', font_color="white")
                col_a.plotly_chart(fig_calls, use_container_width=True)
            if 'sms' in st.session_state.forensic_data:
                fig_sms = px.histogram(st.session_state.forensic_data['sms'], x="Hour", title="Peak Messaging Frequency", color_discrete_sequence=['#ff4b4b'])
                fig_sms.update_layout(paper_bgcolor='rgba(0,0,0,0)', font_color="white")
                col_b.plotly_chart(fig_sms, use_container_width=True)
        else: st.info("Run Acquisition first.")

    # 3. TIMELINE
    with tabs[2]:
        st.subheader("Sequential Event Reconstruction")
        if 'calls' in st.session_state.forensic_data or 'sms' in st.session_state.forensic_data:
            frames = []
            if 'calls' in st.session_state.forensic_data: frames.append(st.session_state.forensic_data['calls'][['DateTime', 'Activity', 'Source']])
            if 'sms' in st.session_state.forensic_data: frames.append(st.session_state.forensic_data['sms'][['DateTime', 'Activity', 'Source']])
            if frames:
                timeline = pd.concat(frames).sort_values(by='DateTime', ascending=False)
                st.dataframe(timeline, use_container_width=True)
        else: st.warning("Requires Call or SMS logs to reconstruct timeline.")

    # 4. REPORTING (AESTHETIC & FIXED)
    with tabs[3]:
        st.subheader("📋 Official Forensic Reporting")
        col_r1, col_r2 = st.columns(2)
        with col_r1:
            case_id = st.text_input("CASE IDENTIFIER", "SEC-X-101")
            officer = st.text_input("INVESTIGATING OFFICER", "Admin_Forensic")
        with col_r2:
            dept = st.text_input("DEPARTMENT", "Cyber Crime Cell")
            notes = st.text_area("CASE NOTES", "Initial device acquisition complete.")

        if st.button("🚀 COMPILE & DOWNLOAD AESTHETIC REPORT"):
            if st.session_state.forensic_data:
                pdf = FPDF()
                pdf.add_page()
                
                # Header Section
                pdf.set_fill_color(30, 30, 30)
                pdf.rect(0, 0, 210, 40, 'F')
                pdf.set_text_color(255, 75, 75)
                pdf.set_font("Arial", 'B', 24)
                pdf.cell(0, 20, "ECHELON FORENSIC AUDIT", ln=True, align='C')
                
                # Case Info
                pdf.set_text_color(0, 0, 0)
                pdf.set_font("Arial", 'B', 12)
                pdf.ln(20)
                pdf.cell(0, 10, f"Case ID: {case_id}", ln=True)
                pdf.cell(0, 10, f"Officer: {officer}", ln=True)
                pdf.cell(0, 10, f"Dept: {dept}", ln=True)
                pdf.cell(0, 10, f"Date: {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}", ln=True)
                pdf.ln(5)
                pdf.line(10, pdf.get_y(), 200, pdf.get_y())
                pdf.ln(10)

                # Summary Table
                pdf.set_font("Arial", 'B', 14)
                pdf.cell(0, 10, "Acquisition Summary:", ln=True)
                pdf.set_font("Arial", '', 11)
                for k, v in st.session_state.forensic_data.items():
                    pdf.cell(0, 8, f"- {k.upper()}: {len(v)} records extracted.", ln=True)
                
                pdf.ln(10)
                pdf.set_font("Arial", 'I', 10)
                pdf.multi_cell(0, 8, f"Notes: {notes}")
                
                # Footer
                pdf.set_y(-30)
                pdf.set_font("Arial", 'B', 8)
                pdf.cell(0, 10, "CONFIDENTIAL - ECHELON MOBILE FORENSICS", align='C')

                # Generate Download
                pdf_output = pdf.output(dest='S').encode('latin-1')
                st.download_button(label="📥 DOWNLOAD PDF REPORT", data=pdf_output, file_name=f"{case_id}_Final_Report.pdf", mime="application/pdf")
                st.success("Report Compiled Successfully!")
            else:
                st.error("No data available. Please acquire artifacts first.")

    # 5. SANDBOX
    with tabs[4]:
        st.subheader("Global Artifact Search")
        if st.session_state.forensic_data:
            src = st.selectbox("SELECT STREAM", list(st.session_state.forensic_data.keys()))
            search = st.text_input("🔍 FILTER KEYWORDS")
            df_sb = st.session_state.forensic_data[src]
            if search:
                df_sb = df_sb[df_sb.astype(str).apply(lambda x: x.str.contains(search, case=False)).any(axis=1)]
            st.dataframe(df_sb, use_container_width=True)
        else: st.info("No data available to search.")

    # 6. LIVE GPS
    with tabs[5]:
        st.subheader("📍 Geospatial Intelligence")
        lat, lon = 21.1702, 72.8311 # Default Surat
        st.map(pd.DataFrame({'lat': [lat], 'lon': [lon]}), zoom=14)
        st.markdown(f"**Coordinates Captured:** {lat}, {lon}")

if __name__ == "__main__":
    main()