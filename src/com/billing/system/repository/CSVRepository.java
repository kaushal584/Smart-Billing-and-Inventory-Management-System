package com.billing.system.repository;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public abstract class CSVRepository<T> implements Repository<T> {
    protected final String filePath;
    protected final Map<String, T> storage = new HashMap<>();

    public CSVRepository(String fileName) {
        this.filePath = fileName;
        load();
    }

    protected abstract T parseLine(String line);

    protected abstract String toCSV(T entity);

    protected abstract String getId(T entity);

    protected void load() {
        storage.clear();
        Path path = Paths.get(filePath);
        if (!Files.exists(path))
            return;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                try {
                    T entity = parseLine(line);
                    storage.put(getId(entity), entity);
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void saveToFile() {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filePath))) {
            for (T entity : storage.values()) {
                bw.write(toCSV(entity));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(T entity) {
        storage.put(getId(entity), entity);
        saveToFile();
    }

    @Override
    public Optional<T> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(String id) {
        storage.remove(id);
        saveToFile();
    }
}
