import sys
import os

# Ensure project root is on PYTHONPATH
PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
if PROJECT_ROOT not in sys.path:
    sys.path.insert(0, PROJECT_ROOT)

import csv

from ANALYSIS.analysis import (
    analyze_calls,
    analyze_messages,
    analyze_apps,
    analyze_location_jumps,
    analyze_timestamp_anomalies
)
from TIMELINE.timeline import build_timeline
import REPORT.report_generator as report_gen

# ---------------- PATH SETUP ----------------
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA_DIR = os.path.join(BASE_DIR, "DATA")

# ---------------- UTIL ----------------
def read_csv(filename):
    path = os.path.join(DATA_DIR, filename)
    data = []
    with open(path, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            data.append(row)
    return data

def pretty(title, items):
    print(f"\n=== {title} ===")
    if not items:
        print("No data found")
        return
    for i, it in enumerate(items, 1):
        print(f"{i}. " + ", ".join(f"{k}: {v}" for k, v in it.items()))

# ---------------- MAIN FLOW ----------------
def extract_all_data():
    # STEP 1: EXTRACTION
    calls = read_csv("calls.csv")
    messages = read_csv("messages.csv")
    apps = read_csv("apps.csv")
    locations = read_csv("location.csv")

    pretty("CALL LOGS", calls)
    pretty("MESSAGES", messages)
    pretty("APPS", apps)
    pretty("LOCATIONS", locations)

    # STEP 2: ANALYSIS
    s_calls = analyze_calls(calls)
    s_msgs = analyze_messages(messages)
    s_apps = analyze_apps(apps)
    s_locations = analyze_location_jumps(locations)

    pretty("SUSPICIOUS CALLS", s_calls)
    pretty("SUSPICIOUS MESSAGES", s_msgs)
    pretty("SUSPICIOUS APPS", s_apps)
    pretty("SUSPICIOUS LOCATIONS", s_locations)

    # Timestamp integrity checks
    ts_anomalies = (
        analyze_timestamp_anomalies(calls) +
        analyze_timestamp_anomalies(messages) +
        analyze_timestamp_anomalies(locations)
    )
    pretty("TIMESTAMP ANOMALIES", ts_anomalies)

    # STEP 3: TIMELINE
    timeline = build_timeline(calls, messages, locations)
    print("\n=== TIMELINE (Chronological) ===")
    for i, e in enumerate(timeline, 1):
        sev = e.get("severity", "NORMAL")
        typ = e.get("type", "EVENT")
        print(f"{i}. [{typ}] [{sev}] {e['time']} -> {e['event']}")

    # STEP 4: REPORT
    report_path = report_gen.generate_report(
        calls,
        messages,
        apps,
        locations,
        s_calls,
        s_msgs,
        s_apps,
        timeline
    )
    print(f"\nReport generated successfully: {report_path}")

# ---------------- ENTRY ----------------
if __name__ == "__main__":
    extract_all_data()