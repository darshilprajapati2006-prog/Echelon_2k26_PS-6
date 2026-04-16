# Java OOP Mobile Forensics Project

This project now supports real ADB-based extraction from a USB-connected Android phone and then analyzes the extracted artifacts using Java OOP classes.

## Real-Time Extraction Scope

- Device info
- Call logs
- SMS
- Contacts
- Installed third-party apps
- Media file listing from shared storage

## Requirements

- Android phone connected with USB cable
- USB debugging enabled
- RSA debugging prompt accepted on the phone
- `adb` available in terminal path

## OOP Concepts Used

- Encapsulation through private fields with getters
- Inheritance using `ForensicRecord` as the abstract base class
- Abstraction through service classes such as `LiveDataExtractor`, `ReportGenerator`, and `ForensicAnalyzer`
- Polymorphism through shared printing and analysis methods for multiple record types

## Compile

```bash
javac -d out $(find src -name "*.java")
```

## Run With Real Device

```bash
java -cp out com.echelon.forensics.MobileForensicsApp
```

## Offline Sample Mode

```bash
java -cp out com.echelon.forensics.MobileForensicsApp --use-sample
```

## Output

- Extracted CSV files: `JavaOOPProject/extracted/`
- Generated report: `JavaOOPProject/output/forensic_report.txt`
