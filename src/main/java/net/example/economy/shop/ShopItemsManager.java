package net.example.economy.shop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;

public class ShopItemsManager {

    public static class Category {
        public String id;
        public String displayName;
        public String icon;
        public List<String> items = new ArrayList<>();
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type CAT_LIST_TYPE = new TypeToken<List<Category>>(){}.getType();
    private static ShopItemsManager INSTANCE;

    public static ShopItemsManager get() {
        if (INSTANCE == null) INSTANCE = new ShopItemsManager();
        return INSTANCE;
    }

    private final List<Category> categories = new ArrayList<>();
    private File file;

    public void setup(MinecraftServer server) {
        Path cfgDir = server.getServerDirectory().resolve("config").resolve("shopandeconomy");
        cfgDir.toFile().mkdirs();
        file = cfgDir.resolve("shop.json").toFile();
        if (!file.exists()) {
            // seed with an example
            Category tools = new Category();
            tools.id = "tools";
            tools.displayName = "Outils";
            tools.icon = "minecraft:iron_pickaxe";
            tools.items.add("minecraft:iron_pickaxe");
            tools.items.add("minecraft:iron_axe");

            Category food = new Category();
            food.id = "food";
            food.displayName = "Nourriture";
            food.icon = "minecraft:bread";
            food.items.add("minecraft:bread");
            food.items.add("minecraft:apple");

            categories.clear();
            categories.add(tools);
            categories.add(food);
            save();
        } else {
            load();
        }
    }

    public void load() {
        try (FileReader r = new FileReader(file)) {
            List<Category> list = GSON.fromJson(r, CAT_LIST_TYPE);
            categories.clear();
            if (list != null) categories.addAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try (FileWriter w = new FileWriter(file)) {
            GSON.toJson(categories, CAT_LIST_TYPE, w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        load();
    }

    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    // Legacy compatibility for ShopMenu
    public List<String> getItems() {
        List<String> all = new ArrayList<>();
        for (Category c : categories) all.addAll(c.items);
        return all;
    }

    public boolean addCategory(String id, String displayName, String icon) {
        if (findCategory(id) != null) return false;
        Category c = new Category();
        c.id = id; c.displayName = displayName; c.icon = icon;
        categories.add(c);
        save();
        return true;
    }

    public boolean removeCategory(String id) {
        Category c = findCategory(id);
        if (c == null) return false;
        categories.remove(c);
        save();
        return true;
    }

    public boolean addItemToCategory(String categoryId, String itemId) {
        Category c = findCategory(categoryId);
        if (c == null) return false;
        if (!isValidItem(itemId)) return false;
        if (!c.items.contains(itemId)) c.items.add(itemId);
        save();
        return true;
    }

    public boolean removeItemFromCategory(String categoryId, String itemId) {
        Category c = findCategory(categoryId);
        if (c == null) return false;
        boolean ok = c.items.remove(itemId);
        if (ok) save();
        return ok;
    }

    private Category findCategory(String id) {
        for (Category c : categories) if (c.id.equalsIgnoreCase(id)) return c;
        return null;
    }

    private boolean isValidItem(String id) {
        try {
            ResourceLocation rl = ResourceLocation.parse(id);
            return BuiltInRegistries.ITEM.getOptional(rl).isPresent();
        } catch (Exception ex) {
            return false;
        }
    }
}
