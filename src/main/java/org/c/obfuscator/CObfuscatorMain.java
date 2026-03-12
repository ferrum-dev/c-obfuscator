package org.c.obfuscator;

public class CObfuscatorMain {

    public static void main(String[] args) throws Exception {
        // Открыть CMD окно и запустить CLI
        if (args.length == 0) {
            openCmdAndRunCLI();
        } else {
            CObfuscatorCLI.main(args);
        }
    }

    private static void openCmdAndRunCLI() throws Exception {
        String javaPath = System.getProperty("java.home") + "\\bin\\java.exe";
        String jarPath = new java.io.File(CObfuscatorMain.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI()).getAbsolutePath();
        
        String command = "chcp 65001 >nul && \"" + javaPath + "\" -cp \"" + jarPath + "\" org.c.obfuscator.CObfuscatorCLI";
        
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start", "cmd.exe", "/k", command);
        pb.start();
        System.exit(0);
    }
}
