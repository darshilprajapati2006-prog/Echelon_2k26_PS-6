from datetime import datetime

def build_timeline(calls, messages, locations):
    """
    Builds a chronological forensic timeline from extracted artifacts.
    Each event includes type and severity for investigation clarity.
    """
    timeline = []

    # ---- CALL EVENTS ----
    for call in calls:
        hour = datetime.strptime(call["time"], "%Y-%m-%d %H:%M").hour
        severity = "HIGH" if 0 <= hour <= 5 else "NORMAL"

        timeline.append({
            "time": call["time"],
            "type": "CALL",
            "severity": severity,
            "event": f"Call to {call['number']} (Duration {call['duration']} sec)"
        })

    # ---- MESSAGE EVENTS ----
    for msg in messages:
        content = msg["message"].lower()
        severity = "HIGH" if ("link" in content or msg["sender"].lower() == "unknown") else "NORMAL"

        timeline.append({
            "time": msg["time"],
            "type": "MESSAGE",
            "severity": severity,
            "event": f"Message from {msg['sender']}: {msg['message']}"
        })

    # ---- LOCATION EVENTS ----
    for loc in locations:
        hour = datetime.strptime(loc["time"], "%Y-%m-%d %H:%M").hour
        severity = "HIGH" if 0 <= hour <= 5 else "NORMAL"

        timeline.append({
            "time": loc["time"],
            "type": "LOCATION",
            "severity": severity,
            "event": f"Location changed to ({loc['latitude']}, {loc['longitude']})"
        })

    # ---- SORT TIMELINE ----
    timeline.sort(key=lambda x: datetime.strptime(x["time"], "%Y-%m-%d %H:%M"))
    return timeline