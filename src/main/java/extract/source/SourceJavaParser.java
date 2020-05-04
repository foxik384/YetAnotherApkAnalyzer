package extract.source;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import property.SourceImportJavaProperty;

import java.util.List;
import java.util.Map;

class SourceJavaParser {
    private static final Map<String, Integer> importsMap;

    private static final int importActivity;
    private static final int importIntent;
    private static final int importIntentFilter;
    private static final int importContentResolver;
    private static final int importDataInputStream;
    private static final int importBufferedReader;
    private static final int importDataOutputStream;
    private static final int importInetSocketAddress;
    private static final int importFile;
    private static final int importStream;
    private static final int importTelephonyManager;
    private static final int importService;
    private static final int importContext;
    private static final int importPackageManager;
    private static final int importSmsManager;
    private static final int importTimer;
    private static final int importBundle;
    private static final int importApplicationInfo;
    private static final int importTimerTask;
    private static final int importFileOutputStream;
    private static final int importNetworkInfo;
    private static final int importConnectivityManager;
    private static final int importLog;

    private static final int totalImportValues;

    static {
        int signIdx = 0;

        importActivity = signIdx++;
        importIntent = signIdx++;
        importIntentFilter = signIdx++;
        importContentResolver = signIdx++;
        importDataInputStream = signIdx++;
        importBufferedReader = signIdx++;
        importDataOutputStream = signIdx++;
        importInetSocketAddress = signIdx++;
        importFile = signIdx++;
        importStream = signIdx++;
        importTelephonyManager = signIdx++;
        importService = signIdx++;
        importContext = signIdx++;
        importPackageManager = signIdx++;
        importSmsManager = signIdx++;
        importTimer = signIdx++;
        importBundle = signIdx++;
        importApplicationInfo = signIdx++;
        importTimerTask = signIdx++;
        importFileOutputStream = signIdx++;
        importNetworkInfo = signIdx++;
        importConnectivityManager = signIdx++;
        importLog = signIdx++;

        totalImportValues = signIdx;

        importsMap = Map.ofEntries(
                Map.entry("android.app.Activity", importActivity),
                Map.entry("android.content.Intent", importIntent),
                Map.entry("android.content.IntentFilter", importIntentFilter),
                Map.entry("android.content.ContentResolver", importContentResolver),
                Map.entry("java.io.DataInputStream", importDataInputStream),
                Map.entry("java.io.BufferedReader", importBufferedReader),
                Map.entry("java.io.DataOutputStream", importDataOutputStream),
                Map.entry("java.net.InetSocketAddress", importInetSocketAddress),
                Map.entry("java.io.File", importFile),
                Map.entry("java.util.stream.Stream", importStream),
                Map.entry("android.telephony.TelephonyManager", importTelephonyManager),
                Map.entry("android.app.Service", importService),
                Map.entry("android.content.Context", importContext),
                Map.entry("android.content.pm.PackageManager", importPackageManager),
                Map.entry("android.telephony.SmsManager", importSmsManager),
                Map.entry("java.util.Timer", importTimer),
                Map.entry("android.os.Bundle", importBundle),
                Map.entry("android.content.pm.ApplicationInfo", importApplicationInfo),
                Map.entry("java.util.TimerTask", importTimerTask),
                Map.entry("java.io.FileOutputStream", importFileOutputStream),
                Map.entry("android.net.NetworkInfo", importNetworkInfo),
                Map.entry("android.net.ConnectivityManager", importConnectivityManager),
                Map.entry("android.util.Log", importLog)
        );
    }

    private final int[] values;

    public SourceJavaParser() {
        values = new int[totalImportValues];
    }

    public void extractInfo(CompilationUnit cu) {
        processImports(cu.getImports());
    }

    private void processImports(List<ImportDeclaration> importDeclarations) {
        for (ImportDeclaration importDeclaration : importDeclarations) {
            Integer valuesIdx = importsMap.get(importDeclaration.getNameAsString());
            if (valuesIdx != null) {
                values[valuesIdx] += 1;
            }
        }
    }

    public void exportInProperties(SourceImportJavaProperty property) {
        property.setActivityImports(values[importActivity]);
        property.setIntentImports(values[importIntent]);
        property.setIntentFilterImports(values[importIntentFilter]);
        property.setContentResolverImports(values[importContentResolver]);
        property.setDataInputStreamImports(values[importDataInputStream]);
        property.setBufferedReaderImports(values[importBufferedReader]);
        property.setDataOutputStreamImports(values[importDataOutputStream]);
        property.setInetSocketAddressImports(values[importInetSocketAddress]);
        property.setFileImports(values[importFile]);
        property.setStreamImports(values[importStream]);
        property.setTelephonyManagerImports(values[importTelephonyManager]);
        property.setServiceImports(values[importService]);
        property.setContextImports(values[importContext]);
        property.setPackageManagerImports(values[importPackageManager]);
        property.setSmsManagerImports(values[importSmsManager]);
        property.setTimerImports(values[importTimer]);
        property.setBundleImports(values[importBundle]);
        property.setApplicationInfoImports(values[importApplicationInfo]);
        property.setTimerTaskImports(values[importTimerTask]);
        property.setFileOutputStreamImports(values[importFileOutputStream]);
        property.setNetworkInfoImports(values[importNetworkInfo]);
        property.setConnectivityManagerImports(values[importConnectivityManager]);
        property.setLogImports(values[importLog]);
    }
}
