package org.c.obfuscator;

import java.nio.file.Files;
import java.nio.file.Path;

public class CSharpObfuscator {

    public static void obfuscate(Path inputFile, Path outputFile) throws Exception {
        byte[] content = Files.readAllBytes(inputFile);
        
        // Apply C# specific obfuscation
        content = obfuscateMetadata(content);
        content = encryptStrings(content);
        content = addAntiDebug(content);
        content = addControlFlow(content);
        
        Files.write(outputFile, content);
    }

    private static byte[] obfuscateMetadata(byte[] content) {
        // Obfuscate .NET metadata
        // Replace readable strings with encrypted versions
        byte[] result = new byte[content.length];
        System.arraycopy(content, 0, result, 0, content.length);
        
        // Simple obfuscation: XOR common strings
        String[] commonStrings = {"Main", "Program", "Class", "Method", "Field"};
        for (String str : commonStrings) {
            byte[] strBytes = str.getBytes();
            for (int i = 0; i < result.length - strBytes.length; i++) {
                boolean match = true;
                for (int j = 0; j < strBytes.length; j++) {
                    if (result[i + j] != strBytes[j]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    for (int j = 0; j < strBytes.length; j++) {
                        result[i + j] = (byte) (result[i + j] ^ 0xFF);
                    }
                }
            }
        }
        
        return result;
    }

    private static byte[] encryptStrings(byte[] content) {
        // Encrypt string literals in .NET assembly
        byte[] result = new byte[content.length];
        System.arraycopy(content, 0, result, 0, content.length);
        
        // Add encryption markers
        for (int i = 0; i < result.length - 4; i++) {
            if (result[i] == 0x1F && result[i + 1] == 0x00) {
                // String length marker in .NET
                int len = (result[i + 2] & 0xFF) | ((result[i + 3] & 0xFF) << 8);
                if (len > 0 && len < 256 && i + 4 + len < result.length) {
                    for (int j = 0; j < len; j++) {
                        result[i + 4 + j] = (byte) (result[i + 4 + j] ^ 0xAA);
                    }
                }
            }
        }
        
        return result;
    }

    private static byte[] addAntiDebug(byte[] content) {
        // Add anti-debug checks to .NET assembly
        // This is a simplified version - real implementation would modify IL code
        byte[] antiDebugMarker = new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF};
        
        byte[] result = new byte[content.length + antiDebugMarker.length];
        System.arraycopy(content, 0, result, 0, content.length);
        System.arraycopy(antiDebugMarker, 0, result, content.length, antiDebugMarker.length);
        
        return result;
    }

    private static byte[] addControlFlow(byte[] content) {
        // Add control flow obfuscation
        byte[] result = new byte[content.length];
        System.arraycopy(content, 0, result, 0, content.length);
        
        // Shuffle some bytes to break linear flow
        for (int i = 0; i < result.length - 8; i += 16) {
            byte temp = result[i];
            result[i] = result[i + 4];
            result[i + 4] = temp;
        }
        
        return result;
    }
}
