package klinik.app;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Pembantu membaca input dari keyboard dengan validasi.
 * Input yang salah (huruf pada kolom angka, nilai kosong, format tanggal salah)
 * tidak membuat program crash; pengguna diminta mengulang input.
 */
public class InputHelper {

    private final Scanner scanner;

    /** Konstruktor default: membaca dari System.in. */
    public InputHelper() {
        this(new Scanner(System.in));
    }

    /** Konstruktor berparameter: sumber input bisa ditentukan sendiri. */
    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Membaca satu baris teks (boleh kosong). Jika input habis (EOF), program ditutup dengan rapi. */
    public String bacaBaris(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println("Input berakhir. Program ditutup.");
            System.exit(0);
        }
        // buang karakter BOM (﻿) yang kadang ikut jika input berasal dari file
        return scanner.nextLine().replace("﻿", "").trim();
    }

    /** Membaca teks yang tidak boleh kosong; diulang sampai pengguna mengisi. */
    public String bacaTeksWajib(String prompt) {
        while (true) {
            String teks = bacaBaris(prompt);
            if (!teks.isEmpty()) {
                return teks;
            }
            System.out.println("  [!] Input tidak boleh kosong, silakan ulangi.");
        }
    }

    /** Membaca bilangan bulat; diulang jika yang diketik bukan angka. */
    public int bacaInt(String prompt) {
        while (true) {
            String teks = bacaBaris(prompt);
            try {
                return Integer.parseInt(teks);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Input harus berupa angka bulat, silakan ulangi.");
            }
        }
    }

    /** Overloading: membaca bilangan bulat dalam rentang min..max (dipakai untuk pilihan menu). */
    public int bacaInt(String prompt, int min, int max) {
        while (true) {
            int nilai = bacaInt(prompt);
            if (nilai >= min && nilai <= max) {
                return nilai;
            }
            System.out.println("  [!] Pilihan harus antara " + min + " dan " + max + ".");
        }
    }

    /** Membaca bilangan desimal (tanpa titik ribuan, contoh 50000 atau 12500.5). */
    public double bacaDouble(String prompt) {
        while (true) {
            String teks = bacaBaris(prompt).replace(',', '.');
            try {
                return Double.parseDouble(teks);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Input harus berupa angka (contoh: 50000), silakan ulangi.");
            }
        }
    }

    /** Membaca jawaban y/t. */
    public boolean bacaYaTidak(String prompt) {
        while (true) {
            String teks = bacaBaris(prompt + " (y/t): ").toLowerCase();
            if (teks.equals("y") || teks.equals("ya")) {
                return true;
            }
            if (teks.equals("t") || teks.equals("tidak") || teks.equals("n")) {
                return false;
            }
            System.out.println("  [!] Jawab dengan y atau t.");
        }
    }

    /** Membaca tanggal format yyyy-mm-dd; kosong berarti hari ini. */
    public LocalDate bacaTanggal(String prompt) {
        while (true) {
            String teks = bacaBaris(prompt + " [yyyy-mm-dd, kosong = hari ini]: ");
            if (teks.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(teks);
            } catch (DateTimeParseException e) {
                System.out.println("  [!] Format tanggal salah. Contoh yang benar: 2026-11-23");
            }
        }
    }
}
