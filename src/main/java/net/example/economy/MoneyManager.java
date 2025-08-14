package net.example.economy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MoneyManager {
    private final Map<UUID, Long> balances = new HashMap<>();
    private File dataFile;

    public long get(UUID id) { return balances.getOrDefault(id, 0L); }
    public void set(UUID id, long amount) { balances.put(id, Math.max(0L, amount)); }
    public void add(UUID id, long amount) { set(id, get(id) + amount); }
    public boolean withdraw(UUID id, long amount) { long cur = get(id); if (cur < amount) return false; set(id, cur - amount); return true; }

    public void deposit(UUID id, long amount) { add(id, amount); }
    public void save() { persist(); }

    public void loadAll(MinecraftServer server) {
        Path dir = server.getServerDirectory().resolve("config").resolve("shopandeconomy");
        dir.toFile().mkdirs();
        dataFile = dir.resolve("balances.json").toFile();
        if (dataFile.exists()) {
            try (FileReader r = new FileReader(dataFile)) {
                Type type = new TypeToken<Map<UUID, Long>>(){}.getType();
                Map<UUID, Long> map = new Gson().fromJson(r, type);
                if (map != null) { balances.clear(); balances.putAll(map); }
            } catch (Exception ignored) {}
        }
    }

    public void saveAll(MinecraftServer server) { persist(); }

    private void persist() {
        if (dataFile == null) return;
        try (FileWriter w = new FileWriter(dataFile)) {
            new Gson().toJson(balances, w);
        } catch (Exception ignored) {}
    }

    public java.util.Map<java.util.UUID, Long> top(int n) {
        return balances.entrySet().stream()
                .sorted((a,b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(n)
                .collect(java.util.stream.Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue(),
                        (a,b) -> a, java.util.LinkedHashMap::new
                ));
    }
}
