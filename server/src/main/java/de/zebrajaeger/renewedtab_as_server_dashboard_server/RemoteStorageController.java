package de.zebrajaeger.renewedtab_as_server_dashboard_server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/config")
@Slf4j
public class RemoteStorageController {

    private final FileStorageService fileStorageService;

    @Autowired
    public RemoteStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    // GET: Gesamte Datenstruktur aus dem Cache (oder Datei) abrufen
    @GetMapping
    public ResponseEntity<Map<String, Object>> getData() {
        try {
            Map<String, Object> data = fileStorageService.loadData();
            return ResponseEntity.ok(data);
        } catch (IOException e) {
            log.error("Could not load data", e);
            return ResponseEntity.status(500).build();
        }
    }

    // POST: Gesamte Datenstruktur speichern und Cache aktualisieren
    @PostMapping
    public ResponseEntity<Void> saveData(@RequestBody Map<String, Object> data) {
        fileStorageService.saveData(data);  // Cache aktualisieren und in Datei speichern
        return ResponseEntity.ok().build();
    }

    // DELETE: Gesamte Datenstruktur löschen (Cache leeren und Datei aktualisieren)
    @DeleteMapping
    public ResponseEntity<Void> clearData() {
        fileStorageService.saveData(Map.of());  // Leere Map in Datei speichern (Cache und Datei leeren)
        return ResponseEntity.ok().build();
    }
}