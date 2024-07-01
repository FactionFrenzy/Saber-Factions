package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DiscUtil {

    private static final Map<String, Lock> LOCKS = new ConcurrentHashMap<>();

    public static byte[] readBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    public static void writeBytes(Path path, byte[] bytes) throws IOException {
        Files.write(path, bytes);
    }

    public static void write(Path path, String content) throws IOException {
        writeBytes(path, content.getBytes(StandardCharsets.UTF_8));
    }

    public static String read(Path path) throws IOException {
        return new String(readBytes(path), StandardCharsets.UTF_8);
    }

    public static boolean writeCatch(Path path, final String content, boolean sync) {
        String name = path.getFileName().toString();
        Lock lock = LOCKS.computeIfAbsent(name, n -> new ReentrantReadWriteLock().writeLock());

        Runnable writeTask = () -> {
            lock.lock();
            try {
                write(path, content);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        };

        if (sync) {
            writeTask.run();
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(FactionsPlugin.getInstance(), writeTask);
        }
        return true;
    }

    public static String readCatch(Path path) {
        try {
            return read(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}