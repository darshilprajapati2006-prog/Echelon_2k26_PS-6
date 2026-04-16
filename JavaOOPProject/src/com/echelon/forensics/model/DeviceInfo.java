package com.echelon.forensics.model;

public class DeviceInfo {
    private final String serialNumber;
    private final String manufacturer;
    private final String model;
    private final String androidVersion;
    private final String sdkVersion;

    public DeviceInfo(String serialNumber, String manufacturer, String model, String androidVersion, String sdkVersion) {
        this.serialNumber = serialNumber;
        this.manufacturer = manufacturer;
        this.model = model;
        this.androidVersion = androidVersion;
        this.sdkVersion = sdkVersion;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public String toDisplayString() {
        return String.format(
                "Serial: %s, Manufacturer: %s, Model: %s, Android: %s, SDK: %s",
                serialNumber,
                manufacturer,
                model,
                androidVersion,
                sdkVersion
        );
    }
}
