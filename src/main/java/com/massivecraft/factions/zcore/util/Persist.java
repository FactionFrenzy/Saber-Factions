package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.MPlugin;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

// TODO: Give better name and place to differentiate from the entity-orm-ish system in "com.massivecraft.core.persist".


public class Persist {

    private final MPlugin plugin;
    public Persist(MPlugin plugin) {
        this.plugin = plugin;
    }

    // GET NAME - What should we call this type of object?
    public static String getName(Class<?> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }

    public static String getName(Object o) {
        return getName(o.getClass());
    }

    public static String getName(Type type) {
        return getName(type.getClass());
    }

    // GET FILE - In which file would we like to store this object?
    public Path getPath(String name) {
        return plugin.getDataFolder().toPath().resolve(name + ".json");
    }

    public Path getPath(Class<?> clazz) {
        return getPath(getName(clazz));
    }

    public Path getPath(Object obj) {
        return getPath(getName(obj));
    }

    public Path getPath(Type type) {
        return getPath(getName(type));
    }

    // NICE WRAPPERS
    public <T> T loadOrSaveDefault(T def, Class<T> clazz) {
        return loadOrSaveDefault(def, clazz, getPath(clazz));
    }

    public <T> T loadOrSaveDefault(T def, Class<T> clazz, String name) {
        return loadOrSaveDefault(def, clazz, getPath(name));
    }

    public <T> T loadOrSaveDefault(T def, Class<T> clazz, Path path) {
        if (Files.notExists(path)) {
            Logger.print("Creating default: " + path, Logger.PrefixType.DEFAULT);
            this.save(def, path);
            return def;
        }

        Optional<T> loaded = this.load(clazz, path);

        if (!loaded.isPresent()) {
            Logger.print("Using default as I failed to load: " + path, Logger.PrefixType.WARNING);

            // backup bad file, so user can attempt to recover their changes from it
            Path backup = path.resolveSibling(path.getFileName() + "_bad");
            try {
                Files.deleteIfExists(backup);
                Files.move(path, backup);
                Logger.print("Backing up copy of bad file to: " + backup, Logger.PrefixType.WARNING);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return def;
        }
        return loaded.get();
    }

    // SAVE
    public boolean save(Object instance) {
        return save(instance, getPath(instance));
    }

    public boolean save(Object instance, String name) {
        return save(instance, getPath(name));
    }

    public boolean save(Object instance, Path path) {
        return DiscUtil.writeCatch(path, plugin.getGson().toJson(instance), false);
    }

    public boolean saveSync(Object instance) {
        return saveSync(instance, getPath(instance));
    }

    public boolean saveSync(Object instance, String name) {
        return saveSync(instance, getPath(name));
    }

    public boolean saveSync(Object instance, Path path) {
        return DiscUtil.writeCatch(path, plugin.getGson().toJson(instance), true);
    }

    // LOAD BY CLASS
    public <T> Optional<T> load(Class<T> clazz) {
        return load(clazz, getPath(clazz));
    }

    public <T> Optional<T> load(Class<T> clazz, String name) {
        return load(clazz, getPath(name));
    }

    public <T> Optional<T> load(Class<T> clazz, Path path) {
        String content = DiscUtil.readCatch(path);
        if (content == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(plugin.getGson().fromJson(content, clazz));
        } catch (Exception ex) {
            Logger.print(ex.getMessage(), Logger.PrefixType.WARNING);
            return Optional.empty();
        }
    }

    // LOAD BY TYPE
    public <T> Optional<T> load(Type typeOfT, String name) {
        return load(typeOfT, getPath(name));
    }

    public <T> Optional<T> load(Type typeOfT, Path path) {
        String content = DiscUtil.readCatch(path);
        if (content == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(plugin.getGson().fromJson(content, typeOfT));
        } catch (Exception ex) {
            Logger.print(ex.getMessage(), Logger.PrefixType.WARNING);
            return Optional.empty();
        }
    }
}