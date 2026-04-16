package com.echelon.forensics;

import com.echelon.forensics.model.AppRecord;
import com.echelon.forensics.model.CallRecord;
import com.echelon.forensics.model.ContactRecord;
import com.echelon.forensics.model.DeviceInfo;
import com.echelon.forensics.model.ExtractionBundle;
import com.echelon.forensics.model.ForensicRecord;
import com.echelon.forensics.model.InvestigationResult;
import com.echelon.forensics.model.LocationRecord;
import com.echelon.forensics.model.MediaRecord;
import com.echelon.forensics.model.MessageRecord;
import com.echelon.forensics.model.TimelineEvent;
import com.echelon.forensics.service.AdbClient;
import com.echelon.forensics.service.ConsolePrinter;
import com.echelon.forensics.service.CsvExporter;
import com.echelon.forensics.service.CsvDataLoader;
import com.echelon.forensics.service.ForensicAnalyzer;
import com.echelon.forensics.service.LiveDataExtractor;
import com.echelon.forensics.service.ReportGenerator;
import com.echelon.forensics.service.TimelineBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MobileForensicsApp {
    public static void main(String[] args) {
        try {
            Path currentDirectory = Path.of("").toAbsolutePath();
            Path projectRoot = currentDirectory.getFileName() != null
                    && "JavaOOPProject".equals(currentDirectory.getFileName().toString())
                    ? currentDirectory.getParent()
                    : currentDirectory;
            boolean useSampleData = args.length > 0 && "--use-sample".equalsIgnoreCase(args[0]);
            Path dataDirectory = projectRoot.resolve("MobileForensicsTool").resolve("DATA");
            Path reportFile = projectRoot.resolve("JavaOOPProject").resolve("output").resolve("forensic_report.txt");
            Path extractedDirectory = projectRoot.resolve("JavaOOPProject").resolve("extracted");

            ForensicAnalyzer analyzer = new ForensicAnalyzer();
            TimelineBuilder timelineBuilder = new TimelineBuilder();
            ReportGenerator reportGenerator = new ReportGenerator();
            ConsolePrinter consolePrinter = new ConsolePrinter();
            DeviceInfo deviceInfo = null;

            List<CallRecord> calls;
            List<MessageRecord> messages;
            List<ContactRecord> contacts;
            List<AppRecord> apps;
            List<MediaRecord> mediaFiles;
            List<LocationRecord> locations = new ArrayList<>();

            if (useSampleData) {
                System.out.println("Loading sample dataset...");
                CsvDataLoader dataLoader = new CsvDataLoader(dataDirectory);
                calls = dataLoader.loadCalls();
                messages = dataLoader.loadMessages();
                apps = dataLoader.loadApps();
                locations = dataLoader.loadLocations();
                contacts = new ArrayList<>();
                mediaFiles = new ArrayList<>();
            } else {
                System.out.println("Checking ADB device connection...");
                AdbClient adbClient = new AdbClient("adb");
                if (!adbClient.isDeviceConnected()) {
                    System.err.println("No Android device detected.");
                    System.err.println("Connect the phone with USB, enable USB debugging, accept the RSA prompt, then run again.");
                    System.err.println("Use '--use-sample' only for offline classroom demo fallback.");
                    return;
                }

                System.out.println("Device detected. Extracting live phone data...");
                LiveDataExtractor extractor = new LiveDataExtractor(adbClient);
                CsvExporter csvExporter = new CsvExporter();
                ExtractionBundle bundle = extractor.extractAll();
                System.out.println("Extraction complete. Saving CSV exports...");
                csvExporter.exportAll(bundle, extractedDirectory);
                System.out.println("CSV export complete. Running forensic analysis...");

                deviceInfo = bundle.getDeviceInfo();
                calls = bundle.getCalls();
                messages = bundle.getMessages();
                contacts = bundle.getContacts();
                apps = bundle.getApps();
                mediaFiles = bundle.getMediaFiles();
            }

            if (deviceInfo != null) {
                consolePrinter.printDeviceInfo(deviceInfo);
            }
            consolePrinter.printRecordSection("CALL LOGS", calls);
            consolePrinter.printRecordSection("MESSAGES", messages);
            consolePrinter.printContacts("CONTACTS", contacts);
            consolePrinter.printAppSection("APPS", apps);
            consolePrinter.printMedia("MEDIA FILES", mediaFiles);
            consolePrinter.printRecordSection("LOCATIONS", locations);

            List<CallRecord> suspiciousCalls = analyzer.findSuspiciousCalls(calls);
            List<MessageRecord> suspiciousMessages = analyzer.findSuspiciousMessages(messages);
            List<AppRecord> suspiciousApps = analyzer.findSuspiciousApps(apps);
            List<LocationRecord> suspiciousLocations = analyzer.findSuspiciousLocations(locations);

            List<ForensicRecord> timestampAnomalies = new ArrayList<>();
            timestampAnomalies.addAll(analyzer.findTimestampAnomalies(calls));
            timestampAnomalies.addAll(analyzer.findTimestampAnomalies(messages));
            timestampAnomalies.addAll(analyzer.findTimestampAnomalies(locations));

            List<TimelineEvent> timeline = timelineBuilder.buildTimeline(calls, messages, locations);

            consolePrinter.printRecordSection("SUSPICIOUS CALLS", suspiciousCalls);
            consolePrinter.printRecordSection("SUSPICIOUS MESSAGES", suspiciousMessages);
            consolePrinter.printAppSection("SUSPICIOUS APPS", suspiciousApps);
            consolePrinter.printRecordSection("SUSPICIOUS LOCATIONS", suspiciousLocations);
            consolePrinter.printRecordSection("TIMESTAMP ANOMALIES", timestampAnomalies);
            consolePrinter.printTimeline(timeline);

            InvestigationResult result = new InvestigationResult(
                    deviceInfo,
                    calls,
                    messages,
                    contacts,
                    apps,
                    mediaFiles,
                    locations,
                    suspiciousCalls,
                    suspiciousMessages,
                    suspiciousApps,
                    suspiciousLocations,
                    timestampAnomalies,
                    timeline
            );

            Path generatedReport = reportGenerator.generateReport(result, reportFile);
            System.out.println("\nReport generated successfully: " + generatedReport);
            if (!useSampleData) {
                System.out.println("Extracted CSV files saved in: " + extractedDirectory);
            }
        } catch (IOException exception) {
            System.err.println("Failed to load forensic data: " + exception.getMessage());
        } catch (Exception exception) {
            System.err.println("Application error: " + exception.getMessage());
        }
    }
}
