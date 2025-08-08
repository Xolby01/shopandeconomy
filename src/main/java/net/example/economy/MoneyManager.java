package net.example.economy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MoneyManager {
    private final Map<UUID, Long> balances = new HashMap<>();
    private final Gson gson = new Gson();

    private File getConfigDir(MinecraftServer server) {
        File config = new File(server.getServerDirectory(), "config/neoforge-economy");
        if (!config.exists()) config.mkdirs();
        return config;
    }

    public void loadAll(MinecraftServer server) {
        try {
            File data = new File(getConfigDir(server), "balances.json");
            if (!data.exists()) return;
            Type type = new TypeToken<Map<String, Long>>(){}.getType();
            Map<String, Long> map = gson.fromJson(new FileReader(data), type);
            for (Map.Entry<String, Long> e : map.entrySet()) {
                balances.put(UUID.fromString(e.getKey()), e.getValue());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveAll(MinecraftServer server) {
        try {
            File data = new File(getConfigDir(server), "balances.json");
            Map<String, Long> map = new HashMap<>();
            for (Map.Entry<UUID, Long> e : balances.entrySet()) map.put(e.getKey().toString(), e.getValue());
            try (FileWriter w = new FileWriter(data)) { gson.toJson(map, w); }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public long get(java.util.UUID id) { return balances.getOrDefault(id, 0L); }
    public void set(java.util.UUID id, long amount) { balances.put(id, amount); }
    public void add(java.util.UUID id, long amount) { balances.put(id, get(id) + amount); }
    public boolean withdraw(java.util.UUID id, long amount) { long cur = get(id); if (cur < amount) return false; set(id, cur - amount); return true; }

    public Map<java.util.UUID, Long> top(int n) {
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
