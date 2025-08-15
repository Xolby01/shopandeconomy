package net.example.economy.shop;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Gestion du shop par catégories, stocké en JSON.
 * Fichier: config/shopandeconomy/shop.json
 */
public class ShopConfig {

    public static class ItemEntry {
        public String item = "minecraft:stone";
        public long buy_price = 10;
        public long sell_price = 5;
    }

    public static class Category {
        public String id;             // identifiant interne (ex: "blocks")
        public String display_name;   // nom affiché (ex: "Blocs")
        public String icon = "minecraft:chest";
        public List<ItemEntry> items = new ArrayList<>();
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LIST_CAT = new TypeToken<List<Category>>(){}.getType();

    private final Path file;
    private List<Category> categories = new ArrayList<>();

    public ShopConfig(Path file) {
        this.file = file;
    }

    public void loadOrCreateDefaults() {
        try {
            Files.createDirectories(file.getParent());
            if (Files.exists(file)) {
                try (Reader r = Files.newBufferedReader(file)) {
                    List<Category> list = GSON.fromJson(r, LIST_CAT);
                    categories = (list != null) ? list : new ArrayList<>();
                }
            } else {
                categories = defaultCategories();
                save();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (categories == null) categories = new ArrayList<>();
        }
    }

    public void save() {
        try (Writer w = Files.newBufferedWriter(file)) {
            GSON.toJson(categories, LIST_CAT, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public Optional<Category> getCategory(String id) {
        for (Category c : categories) if (c.id.equalsIgnoreCase(id)) return Optional.of(c);
        return Optional.empty();
    }

    public boolean addCategory(String id, String displayName, String icon) {
        if (getCategory(id).isPresent()) return false;
        Category c = new Category();
        c.id = id;
        c.display_name = displayName;
        if (icon != null && !icon.isBlank()) c.icon = icon;
        categories.add(c);
        return true;
    }

    public boolean removeCategory(String id) {
        return categories.removeIf(c -> c.id.equalsIgnoreCase(id));
    }

    public boolean setItem(String categoryId, String itemId, long buy, long sell) {
        Category c = getCategory(categoryId).orElse(null);
        if (c == null) return false;
        for (ItemEntry it : c.items) {
            if (it.item.equalsIgnoreCase(itemId)) {
                it.buy_price = buy;
                it.sell_price = sell;
                return true;
            }
        }
        ItemEntry e = new ItemEntry();
        e.item = itemId;
        e.buy_price = buy;
        e.sell_price = sell;
        c.items.add(e);
        return true;
    }

    public boolean removeItem(String categoryId, String itemId) {
        Category c = getCategory(categoryId).orElse(null);
        if (c == null) return false;
        return c.items.removeIf(i -> i.item.equalsIgnoreCase(itemId));
    }

    public Optional<ItemEntry> findItemAnyCategory(String itemId) {
        for (Category c : categories) {
            for (ItemEntry it : c.items) {
                if (it.item.equalsIgnoreCase(itemId)) return Optional.of(it);
            }
        }
        return Optional.empty();
    }

    public List<ItemEntry> getItems(String categoryId) {
        return getCategory(categoryId).map(c -> Collections.unmodifiableList(c.items)).orElse(List.of());
    }

    private static List<Category> defaultCategories() {
        List<Category> list = new ArrayList<>();

        Category blocks = new Category();
        blocks.id = "blocks";
        blocks.display_name = "Blocs";
        blocks.icon = "minecraft:stone";
        ItemEntry stone = new ItemEntry();
        stone.item = "minecraft:stone";
        stone.buy_price = 10;
        stone.sell_price = 5;
        blocks.items.add(stone);
        list.add(blocks);

        Category food = new Category();
        food.id = "food";
        food.display_name = "Nourriture";
        food.icon = "minecraft:bread";
        ItemEntry bread = new ItemEntry();
        bread.item = "minecraft:bread";
        bread.buy_price = 12;
        bread.sell_price = 6;
        food.items.add(bread);
        list.add(food);

        return list;
    }
}
