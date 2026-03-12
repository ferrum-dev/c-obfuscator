package org.c.obfuscator;

import java.nio.file.Files;
import java.nio.file.Path;

public class CObfuscator {

    public static void obfuscateFile(Path inputFile, Path outputFile) throws Exception {
        ExeType.Type type = ExeType.detect(inputFile);
        
        switch (type) {
            case CSHARP:
                CSharpObfuscator.obfuscate(inputFile, outputFile);
                break;
            case CPP:
                CppObfuscator.obfuscate(inputFile, outputFile);
                break;
            default:
                throw new Exception("Unknown executable type");
        }
    }

    public static ExeType.Type detectType(Path inputFile) throws Exception {
        return ExeType.detect(inputFile);
    }
}
