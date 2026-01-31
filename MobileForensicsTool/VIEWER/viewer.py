import os

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA_DIR = os.path.join(BASE_DIR, "DATA")
REPORT_DIR = os.path.join(BASE_DIR, "REPORT")

def view_file(path, title=None):
    print("\n--- READ ONLY VIEW ---")
    if title:
        print(f"{title}\n")

    with open(path, "r", encoding="utf-8") as f:
        lines = f.readlines()

    if not lines:
        print("No data available")
        return

    headers = [h.strip() for h in lines[0].strip().split(",")]
    records = lines[1:]

    for i, record in enumerate(records, 1):
        values = [v.strip() for v in record.strip().split(",")]
        print(f"Record {i}:")
        for h, v in zip(headers, values):
            print(f"  {h.capitalize()} : {v}")
        print("-" * 30)

def viewer_menu():
    while True:
        print("\n=== FORENSIC VIEWER ===")
        print("1. View Call Logs")
        print("2. View Messages")
        print("3. View Apps")
        print("4. View Locations")
        print("5. View Investigation Report")
        print("0. Exit")

        choice = input("Select option: ")

        if choice == "1":
            view_file(os.path.join(DATA_DIR, "calls.csv"), "CALL LOGS")
        elif choice == "2":
            view_file(os.path.join(DATA_DIR, "messages.csv"), "MESSAGES")
        elif choice == "3":
            view_file(os.path.join(DATA_DIR, "apps.csv"), "INSTALLED APPS")
        elif choice == "4":
            view_file(os.path.join(DATA_DIR, "location.csv"), "LOCATION HISTORY")
        elif choice == "5":
            view_file(os.path.join(REPORT_DIR, "forensic_report.txt"), "FORENSIC REPORT")
        elif choice == "0":
            print("Exiting viewer...")
            break
        else:
            print("Invalid choice")

if __name__ == "__main__":
    viewer_menu()