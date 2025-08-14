package net.example.economy.shop;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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

public class ShopItemsManager {

    public static class Entry {
        public String item = "minecraft:stone";
        public long buy_price = 10;
        public long sell_price = 5;
    }

    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<Entry>>() {}.getType();

    public static List<Entry> load(Path file) {
        try {
            if (Files.exists(file)) {
                try (Reader r = Files.newBufferedReader(file)) {
                    List<Entry> list = GSON.fromJson(r, LIST_TYPE);
                    return list != null ? list : new ArrayList<>();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void save(List<Entry> entries, Path file) {
        try {
            Files.createDirectories(file.getParent());
            try (Writer w = Files.newBufferedWriter(file)) {
                GSON.toJson(entries, LIST_TYPE, w);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
            // 1.21.1 : évite le ctor signalé "private" -> tryParse
            ResourceLocation rl = ResourceLocation.tryParse(ns + ":" + path);
            if (rl == null) return Optional.empty();

            Item it = BuiltInRegistries.ITEM.get(rl);
            if (it == null || it == Items.AIR) return Optional.empty();
            return Optional.of(new ItemStack(it));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
