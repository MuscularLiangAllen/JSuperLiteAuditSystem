package com.liangtee.jsuperlite.auditsys.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by liangtee on 2017/7/28.
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static Path mkdir(String path) {

        Path dirPath = null;

        try {
            if(!Files.exists(Paths.get(path))) {
                dirPath = Files.createDirectories(Paths.get(path));
                logger.info(String.format("%s has been created successfully", path));
            } else {
                dirPath = Paths.get(path);
                logger.info(String.format("%s has already existed", path));
            }

            return dirPath;

        } catch (IOException e) {
            e.printStackTrace();
            logger.info(String.format("Create Folder %s failed", path));
            return null;
        }

    }

    public static boolean copy(String src, String dest) {
        final Path srcPath;
        final Path destPath;

        try {
            srcPath = Paths.get(src);
            if(!Files.exists(srcPath)) {
                logger.info(String.format("File: %s", src));
                return false;
            }
            destPath = Paths.get(dest);

            if(Files.isDirectory(srcPath)) {
                Files.walkFileTree(srcPath, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

                        Path subDir = 0 == dir.compareTo(srcPath) ? destPath : destPath.resolve(dir.subpath(srcPath.getNameCount(), dir.getNameCount()));
                        Files.createDirectories(subDir);

                        return super.preVisitDirectory(dir, attrs);
                }
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.copy(file, destPath.resolve(file.subpath(srcPath.getNameCount(), file.getNameCount())));
                        return super.visitFile(file, attrs);
                    }

                });
            } else {
                Files.copy(srcPath, destPath);
            }

            logger.info(String.format("Copy file %s to %s successfully", src, dest));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean moveTo(String srcPath, String destPath) {
        if(copy(srcPath, destPath)) {
            try {
                delete(srcPath);
                logger.info(String.format("Move file %s to %s successfully", srcPath, destPath));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean delete(String path) {

        Path srcPath = Paths.get(path);
        try {
            if(Files.isDirectory(srcPath)) {
                Files.walkFileTree(srcPath, new SimpleFileVisitor<Path>(){
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.deleteIfExists(file);
                        return super.visitFile(file, attrs);
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.deleteIfExists(dir);
                        return super.postVisitDirectory(dir, exc);
                    }
                });
            } else {
                Files.deleteIfExists(srcPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

}
