package net.example.economy.shop;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Reads shop items from config/shopandeconomy/shop_items.json
 */
public class ShopItemsManager {
    public static final Path CONFIG_DIR = Path.of("config", "shopandeconomy");
    public static final Path SHOP_FILE = CONFIG_DIR.resolve("shop_items.json");
    private static final Gson GSON = new Gson();

    public static class Entry {
        public String item;
        public long buy_price = 0;
        public long sell_price = 0;
    }

    private final List<Entry> items = new ArrayList<>();

    public ShopItemsManager() {
        try {
            if (!Files.exists(CONFIG_DIR)) Files.createDirectories(CONFIG_DIR);
            if (!Files.exists(SHOP_FILE)) {
                List<Entry> example = new ArrayList<>();
                Entry e1 = new Entry();
                e1.item = "minecraft:diamond";
                e1.buy_price = 100;
                e1.sell_price = 50;
                example.add(e1);
                Entry e2 = new Entry();
                e2.item = "minecraft:emerald";
                e2.buy_price = 250;
                e2.sell_price = 125;
                example.add(e2);
                try (Writer w = Files.newBufferedWriter(SHOP_FILE)) {
                    GSON.toJson(example, w);
                }
            }
            load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void load() {
        try (Reader r = Files.newBufferedReader(SHOP_FILE)) {
            Type t = new TypeToken<List<Entry>>(){}.getType();
            List<Entry> read = GSON.fromJson(r, t);
            if (read != null) {
                items.clear();
                items.addAll(read);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized List<Entry> getItems() {
        return new ArrayList<>(items);
    }

    public static Optional<ItemStack> toStack(String registryName) {
        try {
            String ns = "minecraft";
            String path = registryName;
            if (registryName.contains(":")) {
                String[] parts = registryName.split(":", 2);
                ns = parts[0];
                path = parts[1];
            }
            net.minecraft.resources.ResourceLocation rl = new net.minecraft.resources.ResourceLocation(ns, path);
            Item it = BuiltInRegistries.ITEM.get(rl);
            if (it == null) return Optional.empty();
            return Optional.of(new ItemStack(it));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

