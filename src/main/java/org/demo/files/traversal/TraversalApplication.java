package org.demo.files.traversal;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TraversalApplication {

    private static final int MAX_DEPTH = 7;
    private static final int MAX_DIRS = 10;
    private static final int MAX_FILES = 4_000_000;

    private static final String ROOT = "test_root";

    private static void createDirsRecursively(String root) throws IOException {

        int[] result = new int[1];
        createDirsRecursivelyHelper(0, root, result);
        log.info("Created {}", result[0]);
    }

    private static void createDirsRecursivelyHelper(int depth, String root, int[] result) throws IOException {

        if (result[0] % 100000 == 0) {
            log.info("Created so far {}", result[0]);
        }

        if (result[0] >= MAX_FILES) {
            throw new IllegalArgumentException();
        }

        if (depth > MAX_DEPTH) {
            return;
        }

        Path rootPath = Paths.get(root);
        for(int i = 0; i < MAX_DIRS; i++) {
            Path newPapth = Paths.get(rootPath.toString(), UUID.randomUUID().toString());
            Files.createDirectories(newPapth);
            result[0]++;

            createDirsRecursivelyHelper(depth + 1, newPapth.toString(), result);
        }

    }

    private static void directoryRaversal(String root) throws IOException {
        int[] count = new int[1];

        Files.walkFileTree(Paths.get(root), new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException {
                count[0]++;
                if (count[0] % 100000 == 0) {
                    log.info("Visisted {} paths {}", count[0], dir.toString());
                    logMemoryUsage();
                }
                return FileVisitResult.CONTINUE;
            }
        });

        log.info("Totally visited paths {}", count[0]);
    }

    private static void logMemoryUsage() {
        log.info("KB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);

    }

    public static void main(String[] args) {

        try {
//            log.info("Started directory creation");
//            try {
//                createDirsRecursively("./" + ROOT);
//            } catch (IllegalArgumentException ie) {
//                log.error("Finished", ie);
//            }
//            log.info("Finished directory creation");

            log.info("Started directory traversal");
            directoryRaversal("./" + ROOT);
            log.info("Finished directory traversal");

        } catch(IOException ioException) {
            log.error("Error occurred", ioException);
        }

    }

}
