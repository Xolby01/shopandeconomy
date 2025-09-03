package net.example.economy.shop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ShopItemsManager {

    public static class Entry {
        public final String categoryId;
        public final String itemId;
        public final long buyPrice;
        public final long sellPrice;

        public Entry(String categoryId, String itemId, long buyPrice, long sellPrice) {
            this.categoryId = categoryId;
            this.itemId = itemId;
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
        }
    }

    static class Category {
        String id;
        String displayName;
        String icon;
        Map<String, Price> items = new LinkedHashMap<>();
    }

    static class Price {
        long buy;
        long sell;
        Price() {}
        Price(long b, long s) { buy = b; sell = s; }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<String, Category>>(){}.getType();

    private static Map<String, Category> categories = new LinkedHashMap<>();
    private static Path configDir;
    private static Path jsonFile;

    public static void ensureLoaded(MinecraftServer server) {
        if (jsonFile == null) {
            configDir = server.getServerDirectory().toPath().resolve("config").resolve("shopandeconomy");
            jsonFile = configDir.resolve("shop.json");
        }
        if (!Files.exists(jsonFile)) {
            try {
                Files.createDirectories(configDir);
                // default content
                Category tools = new Category();
                tools.id = "tools"; tools.displayName = "Tools"; tools.icon = "minecraft:iron_pickaxe";
                tools.items.put("minecraft:iron_pickaxe", new Price(500, 250));
                tools.items.put("minecraft:diamond_pickaxe", new Price(5000, 2500));

                Category food = new Category();
                food.id = "food"; food.displayName = "Food"; food.icon = "minecraft:bread";
                food.items.put("minecraft:bread", new Price(10, 5));
                food.items.put("minecraft:apple", new Price(8, 4));

                Map<String, Category> map = new LinkedHashMap<>();
                map.put(tools.id, tools);
                map.put(food.id, food);
                categories = map;
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (categories.isEmpty()) {
            load();
        }
    }

    public static boolean reload(MinecraftServer server) {
        categories.clear();
        ensureLoaded(server);
        load();
        return true;
    }

    private static void load() {
        try (BufferedReader br = Files.newBufferedReader(jsonFile)) {
            Map<String, Category> map = GSON.fromJson(br, TYPE);
            if (map != null) {
                categories = new LinkedHashMap<>(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try (BufferedWriter bw = Files.newBufferedWriter(jsonFile)) {
            GSON.toJson(categories, TYPE, bw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Entry> listAll() {
        List<Entry> list = new ArrayList<>();
        for (Category c : categories.values()) {
            for (Map.Entry<String, Price> e : c.items.entrySet()) {
                list.add(new Entry(c.id, e.getKey(), e.getValue().buy, e.getValue().sell));
            }
        }
        return list;
    }

    public static List<Entry> listByCategory(String categoryId) {
        Category c = categories.get(categoryId);
        if (c == null) return Collections.emptyList();
        List<Entry> list = new ArrayList<>();
        for (Map.Entry<String, Price> e : c.items.entrySet()) {
            list.add(new Entry(c.id, e.getKey(), e.getValue().buy, e.getValue().sell));
        }
        return list;
    }

    public static Item getItemById(String id) {
        try {
            ResourceLocation rl = ResourceLocation.parse(id);
            return BuiltInRegistries.ITEM.get(rl);
        } catch (Exception ex) {
            return null;
        }
    }

    public static long getBuyPrice(String itemId) {
        for (Category c : categories.values()) {
            Price p = c.items.get(itemId);
            if (p != null) return p.buy;
        }
        return -1;
    }

    public static long getSellPrice(String itemId) {
        for (Category c : categories.values()) {
            Price p = c.items.get(itemId);
            if (p != null) return p.sell;
        }
        return -1;
    }

    public static boolean addCategory(String id, String displayName, String icon) {
        if (categories.containsKey(id)) return false;
        Category c = new Category();
        c.id = id; c.displayName = displayName; c.icon = icon;
        categories.put(id, c);
        save();
        return true;
    }

    public static boolean removeCategory(String id) {
        Category prev = categories.remove(id);
        if (prev != null) {
            save();
            return true;
        }
        return false;
    }

    public static boolean addItem(String category, String itemId, long buy, long sell) {
        Category c = categories.get(category);
        if (c == null) return false;
        c.items.put(itemId, new Price(buy, sell));
        save();
        return true;
    }

    public static boolean removeItem(String category, String itemId) {
        Category c = categories.get(category);
        if (c == null) return false;
        Price prev = c.items.remove(itemId);
        if (prev != null) {
            save();
            return true;
        }
        return false;
    }
}