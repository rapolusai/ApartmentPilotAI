package com.rapolus.apartmentpilotai.data;

/** Development-only endpoint selection; release builds keep their configured HTTPS endpoint. */
public final class LocalEndpoint {
    private LocalEndpoint() {}
    public static String defaultUrl(String fingerprint,String model,String hardware) {
        String device=(fingerprint+" "+model+" "+hardware).toLowerCase(java.util.Locale.ROOT);
        boolean emulator=device.contains("generic")||device.contains("emulator")||device.contains("sdk_gphone")||device.contains("goldfish")||device.contains("ranchu");
        return "http://"+(emulator?"10.0.2.2":"127.0.0.1")+":8080/api/v1";
    }
}
