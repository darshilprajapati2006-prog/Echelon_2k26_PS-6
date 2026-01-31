from datetime import datetime

def is_night_time(time_str):
    time = datetime.strptime(time_str, "%Y-%m-%d %H:%M")
    return time.hour >= 0 and time.hour <= 5


def analyze_calls(calls):
    suspicious_calls = []
    for call in calls:
        if is_night_time(call["time"]):
            suspicious_calls.append(call)
    return suspicious_calls


def analyze_messages(messages):
    suspicious_messages = []
    for msg in messages:
        if "link" in msg["message"].lower() or msg["sender"].lower() == "unknown":
            suspicious_messages.append(msg)
    return suspicious_messages


def analyze_apps(apps):
    suspicious_apps = []
    for app in apps:
        if app["permission"].lower() == "full access":
            suspicious_apps.append(app)
    return suspicious_apps


# -------- EXTRA FORENSIC CHECKS (ENHANCED ANALYSIS) --------

def analyze_location_jumps(locations):
    """
    Detects suspicious sudden location changes (basic heuristic).
    """
    suspicious_locations = []
    for loc in locations:
        try:
            hour = datetime.strptime(loc["time"], "%Y-%m-%d %H:%M").hour
            if hour >= 0 and hour <= 5:
                suspicious_locations.append(loc)
        except Exception:
            continue
    return suspicious_locations


def analyze_timestamp_anomalies(records, time_key="time"):
    """
    Detects invalid or future timestamps.
    """
    anomalies = []
    now = datetime.now()

    for rec in records:
        try:
            t = datetime.strptime(rec[time_key], "%Y-%m-%d %H:%M")
            if t > now:
                anomalies.append(rec)
        except Exception:
            anomalies.append(rec)

    return anomalies