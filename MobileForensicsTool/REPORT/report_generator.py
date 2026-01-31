import os
from datetime import datetime

def generate_report(
    calls, messages, apps, locations,
    suspicious_calls, suspicious_messages, suspicious_apps,
    timeline
):
    """
    Generates a clean, professional Mobile Forensics Investigation Report.
    Output: REPORT/forensic_report.txt
    """

    # ---- SAFE PATH HANDLING ----
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    report_dir = os.path.join(base_dir, "REPORT")
    os.makedirs(report_dir, exist_ok=True)
    report_path = os.path.join(report_dir, "forensic_report.txt")

    lines = []

    # ---- HEADER ----
    lines.append("MOBILE FORENSICS INVESTIGATION REPORT\n")
    lines.append("=" * 60 + "\n")
    lines.append(f"Generated On : {datetime.now()}\n\n")

    # ---- 1. CASE SUMMARY ----
    lines.append("1. CASE SUMMARY\n")
    lines.append("-" * 60 + "\n")
    lines.append(
        "This report documents the results of a mobile forensic investigation.\n"
        "The objective was to safely extract mobile data, identify suspicious\n"
        "activities, and reconstruct a chronological timeline without modifying\n"
        "original evidence.\n\n"
    )

    # ---- 2. EXTRACTED DATA OVERVIEW ----
    lines.append("2. EXTRACTED DATA OVERVIEW\n")
    lines.append("-" * 60 + "\n")
    lines.append(f"Total Call Records     : {len(calls)}\n")
    lines.append(f"Total Messages         : {len(messages)}\n")
    lines.append(f"Installed Applications : {len(apps)}\n")
    lines.append(f"Location Records       : {len(locations)}\n\n")

    # ---- 3. SUSPICIOUS FINDINGS ----
    lines.append("3. SUSPICIOUS FINDINGS SUMMARY\n")
    lines.append("-" * 60 + "\n")
    lines.append(f"Suspicious Calls    : {len(suspicious_calls)}\n")
    lines.append(f"Suspicious Messages : {len(suspicious_messages)}\n")
    lines.append(f"Suspicious Apps     : {len(suspicious_apps)}\n\n")

    # ---- 3.1 Suspicious Call Details ----
    if suspicious_calls:
        lines.append("3.1 Suspicious Call Details\n")
        for c in suspicious_calls:
            lines.append(
                f"- Number: {c['number']}, Time: {c['time']}, "
                f"Duration: {c['duration']} sec\n"
            )
        lines.append("\n")

    # ---- 3.2 Suspicious Message Details ----
    if suspicious_messages:
        lines.append("3.2 Suspicious Message Details\n")
        for m in suspicious_messages:
            lines.append(
                f"- Sender: {m['sender']}, Time: {m['time']}, "
                f"Content: {m['message']}\n"
            )
        lines.append("\n")

    # ---- 3.3 Suspicious App Details ----
    if suspicious_apps:
        lines.append("3.3 Suspicious Application Details\n")
        for a in suspicious_apps:
            lines.append(
                f"- App Name: {a['app_name']}, Permission: {a['permission']}\n"
            )
        lines.append("\n")

    # ---- 4. TIMELINE RECONSTRUCTION ----
    lines.append("4. TIMELINE RECONSTRUCTION\n")
    lines.append("-" * 60 + "\n")
    for e in timeline:
        severity = e.get("severity", "NORMAL")
        event_type = e.get("type", "EVENT")
        lines.append(
            f"{e['time']} | {event_type} | {severity} | {e['event']}\n"
        )

    # ---- 5. EVIDENCE INTEGRITY NOTE ----
    lines.append("\n5. EVIDENCE INTEGRITY & LIMITATIONS\n")
    lines.append("-" * 60 + "\n")
    lines.append(
        "• All analysis was performed in read-only mode.\n"
        "• Original mobile data was never modified.\n"
        "• This tool is an academic prototype intended for demonstration purposes.\n"
    )

    # ---- WRITE REPORT ----
    with open(report_path, "w", encoding="utf-8") as f:
        f.writelines(lines)

    return report_path