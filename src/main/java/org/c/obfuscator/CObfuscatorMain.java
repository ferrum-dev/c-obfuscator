package org.c.obfuscator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CObfuscatorMain {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java -jar c-obfuscator-1.0.0.jar <input.exe> <output.exe>");
            System.exit(1);
        }
        
        Path inputFile = Paths.get(args[0]);
        Path outputFile = Paths.get(args[1]);
        
        if (!Files.exists(inputFile)) {
            System.out.println("Error: Input file not found");
            System.exit(1);
        }
        
        System.out.println("Detecting type...");
        ExeType.Type type = CObfuscator.detectType(inputFile);
        System.out.println("Type: " + type.displayName);
        
        System.out.println("Obfuscating...");
        long start = System.currentTimeMillis();
        CObfuscator.obfuscateFile(inputFile, outputFile);
        long elapsed = System.currentTimeMillis() - start;
        
        System.out.println("Done in " + elapsed + "ms");
        System.out.println("Output: " + outputFile.toAbsolutePath());
    }
}
