package org.c.obfuscator;

import java.nio.file.Files;
import java.nio.file.Path;

public class ExeType {
    public enum Type {
        CSHARP("C#"),
        CPP("C++"),
        UNKNOWN("Unknown");
        
        public final String displayName;
        Type(String displayName) {
            this.displayName = displayName;
        }
    }

    public static Type detect(Path exePath) throws Exception {
        byte[] header = new byte[512];
        int bytesRead = Files.readAllBytes(exePath).length;
        if (bytesRead < 512) {
            System.arraycopy(Files.readAllBytes(exePath), 0, header, 0, bytesRead);
        } else {
            System.arraycopy(Files.readAllBytes(exePath), 0, header, 0, 512);
        }
        
        // Check for .NET metadata (C#)
        if (hasDotNetMetadata(header)) {
            return Type.CSHARP;
        }
        
        // Check for native C++ indicators
        if (hasNativeIndicators(header)) {
            return Type.CPP;
        }
        
        return Type.UNKNOWN;
    }

    private static boolean hasDotNetMetadata(byte[] header) {
        // Look for .NET CLR Runtime Header signature
        // Typically found in PE header at offset 0x3C
        if (header.length < 0x3C + 4) return false;
        
        int peOffset = (header[0x3C] & 0xFF) | ((header[0x3D] & 0xFF) << 8);
        if (peOffset < 0 || peOffset > header.length - 32) return false;
        
        // Check for PE signature
        if (header[peOffset] != 'P' || header[peOffset + 1] != 'E') return false;
        
        // Check for .NET CLR Runtime Header in Data Directories
        // This is a simplified check - look for common .NET patterns
        String headerStr = new String(header);
        return headerStr.contains(".text") && headerStr.contains(".rsrc") && 
               (headerStr.contains(".reloc") || headerStr.contains(".data"));
    }

    private static boolean hasNativeIndicators(byte[] header) {
        // Check for native C++ indicators
        // Look for common C++ runtime patterns
        String headerStr = new String(header);
        return headerStr.contains("MSVCRT") || headerStr.contains("kernel32") ||
               headerStr.contains("user32") || headerStr.contains("advapi32");
    }
}
