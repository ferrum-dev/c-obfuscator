package org.c.obfuscator;

import java.nio.file.Files;
import java.nio.file.Path;

public class CppObfuscator {

    public static void obfuscate(Path inputFile, Path outputFile) throws Exception {
        byte[] content = Files.readAllBytes(inputFile);
        
        // Apply C++ specific obfuscation
        content = obfuscateSymbols(content);
        content = encryptStrings(content);
        content = addAntiDebug(content);
        content = addJunkCode(content);
        
        Files.write(outputFile, content);
    }

    private static byte[] obfuscateSymbols(byte[] content) {
        // Obfuscate exported symbols and function names
        byte[] result = new byte[content.length];
        System.arraycopy(content, 0, result, 0, content.length);
        
        // Replace common function names with garbage
        String[] commonFuncs = {"main", "WinMain", "DllMain", "printf", "malloc", "free"};
        for (String func : commonFuncs) {
            byte[] funcBytes = func.getBytes();
            for (int i = 0; i < result.length - funcBytes.length; i++) {
                boolean match = true;
                for (int j = 0; j < funcBytes.length; j++) {
                    if (result[i + j] != funcBytes[j]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    // Replace with random bytes
                    for (int j = 0; j < funcBytes.length; j++) {
                        result[i + j] = (byte) (Math.random() * 256);
                    }
                }
            }
        }
        
        return result;
    }

    private static byte[] encryptStrings(byte[] content) {
        // Encrypt string literals in native code
        byte[] result = new byte[content.length];
        System.arraycopy(content, 0, result, 0, content.length);
        
        // Look for ASCII strings and encrypt them
        for (int i = 0; i < result.length - 4; i++) {
            // Check for printable ASCII sequence
            int asciiLen = 0;
            for (int j = i; j < result.length && asciiLen < 256; j++) {
                byte b = result[j];
                if ((b >= 32 && b <= 126) || b == 0) {
                    if (b == 0) break;
                    asciiLen++;
                } else {
                    break;
                }
            }
            
            // If we found a string, encrypt it
            if (asciiLen > 4) {
                for (int j = 0; j < asciiLen; j++) {
                    result[i + j] = (byte) (result[i + j] ^ 0x55);
                }
                i += asciiLen;
            }
        }
        
        return result;
    }

    private static byte[] addAntiDebug(byte[] content) {
        // Add anti-debug code patterns
        byte[] antiDebugPattern = new byte[]{
            (byte) 0x64, (byte) 0xA1, (byte) 0x30, (byte) 0x00, // mov eax, fs:[30h]
            (byte) 0x8B, (byte) 0x40, (byte) 0x0C,             // mov eax, [eax+0Ch]
            (byte) 0x8B, (byte) 0x40, (byte) 0x14              // mov eax, [eax+14h]
        };
        
        byte[] result = new byte[content.length + antiDebugPattern.length];
        System.arraycopy(content, 0, result, 0, content.length);
        System.arraycopy(antiDebugPattern, 0, result, content.length, antiDebugPattern.length);
        
        return result;
    }

    private static byte[] addJunkCode(byte[] content) {
        // Add junk code to increase size and confuse analysis
        byte[] junk = new byte[256];
        for (int i = 0; i < junk.length; i++) {
            junk[i] = (byte) (Math.random() * 256);
        }
        
        byte[] result = new byte[content.length + junk.length];
        System.arraycopy(content, 0, result, 0, content.length);
        System.arraycopy(junk, 0, result, content.length, junk.length);
        
        return result;
    }
}
