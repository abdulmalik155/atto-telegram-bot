package api.auto.generate.table.utill;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Scanner;

public class ScannerUtil {
    public static final Scanner NUM = new Scanner(System.in);
    public static final Scanner STR = new Scanner(System.in);

    public static String getString(String message) {
        System.out.print(message);
        return STR.next().trim();
    }

    public static Integer getInteger(String message) {
        System.out.print(message);
        return NUM.nextInt();
    }
    public static Double getDouble(String message) {
        System.out.print(message);
        return NUM.nextDouble();
    }

    public static int getOption(String message) {
        System.out.print(message + " ");
        return NUM.nextInt();
    }

    public static String generateCode() {
        String confirmationCode = String.valueOf(new Random().nextInt(10000, 99999));
        System.out.println(confirmationCode + " code for confirmation enter under 1 minute!");
        return confirmationCode;
    }
    public static String generateCodeForConfirmation() {
        return String.valueOf(new Random().nextInt(10000, 99999));
    }

}
