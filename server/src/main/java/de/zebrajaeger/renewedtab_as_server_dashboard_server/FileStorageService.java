package de.zebrajaeger.renewedtab_as_server_dashboard_server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j // Lombok-Annotation für den SLF4J-Logger
@Service
public class FileStorageService {

    private final Path filePath;
    private final ObjectMapper objectMapper;
    private Map<String, Object> cache;
    private Timer saveTimer;
    private final long saveDelayMs;

    public FileStorageService(@Value("${storage.path}") String filePath,
                              @Value("${storage.save.delay:10000}") long saveDelayMs) {
        this.filePath = Paths.get(filePath);
        this.objectMapper = new ObjectMapper();
        this.cache = null;
        this.saveTimer = new Timer(true); // Timer als Daemon-Thread
        this.saveDelayMs = saveDelayMs;
    }

    // Daten aus der Datei laden und den Cache initialisieren (nur einmal)
    private void loadDataIfNecessary() throws IOException {
        if (cache == null) {
            if (!Files.exists(filePath)) {
                log.info("Datei '{}' existiert nicht. Initialisiere leeren Speicher.", filePath);
                cache = new HashMap<>();
            } else {
                log.info("Lade Daten aus der Datei '{}'.", filePath);
                try {
                    cache = objectMapper.readValue(new File(filePath.toString()), new TypeReference<Map<String, Object>>() {
                    });
                    log.info("Daten erfolgreich aus der Datei geladen.");
                } catch (IOException e) {
                    log.error("Fehler beim Laden der Daten aus der Datei '{}'.", filePath, e);
                    throw e;
                }
            }
        }
    }

    // Daten aus dem Cache laden (oder initial aus der Datei)
    public Map<String, Object> loadData() throws IOException {
        loadDataIfNecessary();
        return cache;
    }

    // Daten speichern und Cache aktualisieren
    public void saveData(Map<String, Object> data) {
        cache = data;  // Cache aktualisieren
        scheduleSave(); // Verzögertes Speichern planen
    }

    // Timer zum verzögerten Speichern
    private void scheduleSave() {
        if (saveTimer != null) {
            saveTimer.cancel(); // Vorherige Timer abbrechen, um mehrfaches Speichern zu vermeiden
        }

        saveTimer = new Timer(true); // Neuer Timer
        saveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    saveCache(); // Cache in Datei speichern
                } catch (IOException e) {
                    log.error("Fehler beim verzögerten Speichern des Caches.", e);
                }
            }
        }, saveDelayMs);

        log.info("Speichern des Caches in {} Sekunden verzögert.", saveDelayMs / 1000);
    }

    // Sicherstellen, dass das Verzeichnis existiert und gegebenenfalls erstellen
    private void ensureDirectoryExists() throws IOException {
        Path directoryPath = filePath.getParent();
        if (directoryPath != null && !Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
                log.info("Verzeichnis '{}' erfolgreich erstellt.", directoryPath);
            } catch (IOException e) {
                log.error("Fehler beim Erstellen des Verzeichnisses '{}'.", directoryPath, e);
                throw e;
            }
        }
    }

    // Speichern des Caches (direkt ohne Verzögerung)
    public void saveCache() throws IOException {
        ensureDirectoryExists();

        if (cache != null) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath.toString()), cache);  // In Datei speichern
            log.info("Cache erfolgreich in der Datei '{}' gespeichert.", filePath);
        }
    }
}
