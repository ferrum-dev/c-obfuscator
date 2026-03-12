package org.c.obfuscator;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class CObfuscatorCLI {
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";

    private static Scanner scanner;
    private static String inputPath = "";
    private static String outputPath = "";

    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
        
        if (args.length >= 2) {
            cliMode(args[0], args[1]);
        } else {
            showMenu();
        }
    }

    private static void showMenu() {
        while (true) {
            clearScreen();
            printHeader();
            
            System.out.println(CYAN + "╔════════════════════════════════════════╗" + RESET);
            System.out.println(CYAN + "║  C/C++ OBFUSCATOR - ГЛАВНОЕ МЕНЮ       ║" + RESET);
            System.out.println(CYAN + "╚════════════════════════════════════════╝" + RESET);
            System.out.println();
            
            System.out.println(BLUE + "  1." + RESET + " Выбрать входной EXE файл");
            System.out.println(BLUE + "  2." + RESET + " Выбрать выходной EXE файл");
            System.out.println(BLUE + "  3." + RESET + " Использовать папку входного файла");
            System.out.println(BLUE + "  4." + RESET + " Показать текущие пути");
            System.out.println(BLUE + "  5." + RESET + " Запустить обфускацию");
            System.out.println(BLUE + "  0." + RESET + " Выход");
            System.out.println();
            
            System.out.print(YELLOW + "Выберите опцию: " + RESET);
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1": selectInputFile(); break;
                case "2": selectOutputFile(); break;
                case "3": useInputFolder(); break;
                case "4": showPaths(); break;
                case "5": startObfuscation(); break;
                case "0": System.out.println(GREEN + "До свидания!" + RESET); System.exit(0);
                default: System.out.println(RED + "Неверный выбор!" + RESET); pause();
            }
        }
    }

    private static void selectInputFile() {
        clearScreen();
        printHeader();
        System.out.println(CYAN + "═══ ВЫБОР ВХОДНОГО ФАЙЛА ═══" + RESET);
        System.out.println();
        System.out.print("Введите путь к EXE файлу: ");
        String path = scanner.nextLine().trim();
        
        File file = new File(path);
        if (!file.exists()) {
            System.out.println(RED + "✗ Файл не найден!" + RESET);
            pause();
            return;
        }
        
        if (!path.endsWith(".exe")) {
            System.out.println(RED + "✗ Это не EXE файл!" + RESET);
            pause();
            return;
        }
        
        inputPath = path;
        outputPath = "";
        
        try {
            ExeType.Type type = CObfuscator.detectType(Paths.get(path));
            System.out.println(GREEN + "✓ Файл выбран: " + path + RESET);
            System.out.println(MAGENTA + "  Тип: " + type.displayName + RESET);
        } catch (Exception e) {
            System.out.println(GREEN + "✓ Файл выбран: " + path + RESET);
        }
        
        pause();
    }

    private static void selectOutputFile() {
        if (inputPath.isEmpty()) {
            System.out.println(RED + "✗ Сначала выберите входной файл!" + RESET);
            pause();
            return;
        }
        
        clearScreen();
        printHeader();
        System.out.println(CYAN + "═══ ВЫБОР ВЫХОДНОГО ФАЙЛА ═══" + RESET);
        System.out.println();
        System.out.print("Введите путь для сохранения: ");
        String path = scanner.nextLine().trim();
        
        if (!path.endsWith(".exe")) {
            path += ".exe";
        }
        
        outputPath = path;
        System.out.println(GREEN + "✓ Выходной файл: " + path + RESET);
        pause();
    }

    private static void useInputFolder() {
        if (inputPath.isEmpty()) {
            System.out.println(RED + "✗ Сначала выберите входной файл!" + RESET);
            pause();
            return;
        }
        
        File inputFile = new File(inputPath);
        String fileName = inputFile.getName();
        int dotIndex = fileName.lastIndexOf('.');
        String baseName = dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
        
        outputPath = inputFile.getParent() + File.separator + baseName + "_obf.exe";
        System.out.println(GREEN + "✓ Выходной файл: " + outputPath + RESET);
        pause();
    }

    private static void showPaths() {
        clearScreen();
        printHeader();
        System.out.println(CYAN + "═══ ТЕКУЩИЕ ПУТИ ═══" + RESET);
        System.out.println();
        
        if (inputPath.isEmpty()) {
            System.out.println(RED + "✗ Входной файл: не выбран" + RESET);
        } else {
            System.out.println(GREEN + "✓ Входной файл:" + RESET);
            System.out.println("  " + inputPath);
        }
        
        System.out.println();
        
        if (outputPath.isEmpty()) {
            System.out.println(RED + "✗ Выходной файл: не выбран" + RESET);
        } else {
            System.out.println(GREEN + "✓ Выходной файл:" + RESET);
            System.out.println("  " + outputPath);
        }
        
        System.out.println();
        pause();
    }

    private static void startObfuscation() {
        if (inputPath.isEmpty()) {
            System.out.println(RED + "✗ Выберите входной файл!" + RESET);
            pause();
            return;
        }
        
        if (outputPath.isEmpty()) {
            useInputFolder();
        }
        
        clearScreen();
        printHeader();
        System.out.println(CYAN + "═══ ОБФУСКАЦИЯ ═══" + RESET);
        System.out.println();
        
        try {
            long startTime = System.currentTimeMillis();
            long inputSize = Files.size(Paths.get(inputPath));
            
            ExeType.Type type = CObfuscator.detectType(Paths.get(inputPath));
            System.out.println(MAGENTA + "Тип: " + type.displayName + RESET);
            System.out.println();
            System.out.println(YELLOW + "⏳ Обфускация в процессе..." + RESET);
            System.out.println();
            
            CObfuscator.obfuscateFile(Paths.get(inputPath), Paths.get(outputPath));
            
            long outputSize = Files.size(Paths.get(outputPath));
            long elapsed = System.currentTimeMillis() - startTime;
            
            System.out.println(GREEN + "✓ Обфускация завершена!" + RESET);
            System.out.println();
            System.out.println(BLUE + "Статистика:" + RESET);
            System.out.println("  Входной размер:  " + formatSize(inputSize));
            System.out.println("  Выходной размер: " + formatSize(outputSize));
            System.out.println("  Время:           " + elapsed + "ms");
            System.out.println("  Путь:            " + outputPath);
            System.out.println();
            System.out.println(GREEN + "✓ Файл сохранен успешно!" + RESET);
            
        } catch (Exception e) {
            System.out.println(RED + "✗ Ошибка: " + e.getMessage() + RESET);
        }
        
        pause();
    }

    private static void cliMode(String input, String output) {
        try {
            System.out.println(CYAN + "╔════════════════════════════════════════╗" + RESET);
            System.out.println(CYAN + "║  C/C++ OBFUSCATOR - CLI MODE           ║" + RESET);
            System.out.println(CYAN + "╚════════════════════════════════════════╝" + RESET);
            System.out.println();
            
            File inputFile = new File(input);
            if (!inputFile.exists()) {
                System.out.println(RED + "✗ Входной файл не найден: " + input + RESET);
                System.exit(1);
            }
            
            ExeType.Type type = CObfuscator.detectType(Paths.get(input));
            System.out.println(MAGENTA + "Тип: " + type.displayName + RESET);
            System.out.println();
            System.out.println(YELLOW + "⏳ Обфускация в процессе..." + RESET);
            
            long startTime = System.currentTimeMillis();
            long inputSize = Files.size(Paths.get(input));
            
            CObfuscator.obfuscateFile(Paths.get(input), Paths.get(output));
            
            long outputSize = Files.size(Paths.get(output));
            long elapsed = System.currentTimeMillis() - startTime;
            
            System.out.println();
            System.out.println(GREEN + "✓ Обфускация завершена!" + RESET);
            System.out.println();
            System.out.println(BLUE + "Статистика:" + RESET);
            System.out.println("  Входной размер:  " + formatSize(inputSize));
            System.out.println("  Выходной размер: " + formatSize(outputSize));
            System.out.println("  Время:           " + elapsed + "ms");
            System.out.println("  Выход:           " + output);
            
        } catch (Exception e) {
            System.out.println(RED + "✗ Ошибка: " + e.getMessage() + RESET);
            System.exit(1);
        }
    }

    private static void printHeader() {
        System.out.println(CYAN + "╔════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║  🔓 C/C++ OBFUSCATOR v1.0.0            ║" + RESET);
        System.out.println(CYAN + "║  Native Code Obfuscation               ║" + RESET);
        System.out.println(CYAN + "╚════════════════════════════════════════╝" + RESET);
        System.out.println();
    }

    private static String formatSize(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return String.format("%.2f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }

    private static void pause() {
        System.out.println();
        System.out.print(YELLOW + "Нажмите Enter для продолжения..." + RESET);
        scanner.nextLine();
    }
}
