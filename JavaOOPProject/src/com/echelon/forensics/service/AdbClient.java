package com.echelon.forensics.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AdbClient {
    private final String adbExecutable;

    public AdbClient(String adbExecutable) {
        this.adbExecutable = adbExecutable;
    }

    public boolean isDeviceConnected() throws IOException, InterruptedException {
        List<String> output = execute("devices");
        for (String line : output) {
            if (line.endsWith("\tdevice")) {
                return true;
            }
        }
        return false;
    }

    public String getConnectedSerial() throws IOException, InterruptedException {
        List<String> output = execute("devices");
        for (String line : output) {
            if (line.endsWith("\tdevice")) {
                return line.split("\\s+")[0];
            }
        }
        return "UNKNOWN";
    }

    public List<String> shell(String... command) throws IOException, InterruptedException {
        List<String> fullCommand = new ArrayList<>();
        fullCommand.add("shell");
        for (String part : command) {
            fullCommand.add(part);
        }
        return execute(fullCommand.toArray(String[]::new));
    }

    public List<String> execute(String... command) throws IOException, InterruptedException {
        List<String> fullCommand = new ArrayList<>();
        fullCommand.add(adbExecutable);
        for (String part : command) {
            fullCommand.add(part);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(fullCommand);
        Process process = processBuilder.start();
        var executor = Executors.newFixedThreadPool(2);
        Future<List<String>> stdoutFuture = executor.submit(() -> readLines(process.getInputStream()));
        Future<List<String>> stderrFuture = executor.submit(() -> readLines(process.getErrorStream()));

        int exitCode = process.waitFor();
        List<String> stdout = getFutureResult(stdoutFuture);
        List<String> stderr = getFutureResult(stderrFuture);
        executor.shutdown();

        if (exitCode != 0) {
            String errorMessage = String.join(System.lineSeparator(), stderr);
            throw new IOException(errorMessage.isBlank() ? "ADB command failed" : errorMessage);
        }

        return stdout;
    }

    private List<String> readLines(InputStream inputStream) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private List<String> getFutureResult(Future<List<String>> future) throws IOException, InterruptedException {
        try {
            return future.get(30, TimeUnit.SECONDS);
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof IOException ioException) {
                throw ioException;
            }
            throw new IOException("Failed to read ADB output", cause);
        } catch (TimeoutException exception) {
            throw new IOException("Timed out while reading ADB output", exception);
        }
    }
}
