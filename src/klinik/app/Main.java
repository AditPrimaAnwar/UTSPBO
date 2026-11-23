package klinik.app;

import java.time.LocalDate;
import java.util.List;

import klinik.adt.SortedList;
import klinik.model.Dokter;
import klinik.model.Klinik;
import klinik.model.Kunjungan;
import klinik.model.Pasien;

/**
 * Class Main: menampilkan menu console dan meneruskan perintah pengguna ke Klinik.
 * Semua pelanggaran aturan bisnis ditangkap di sini dan ditampilkan sebagai
 * pesan kesalahan, sehingga program tidak berhenti.
 */
public class Main {

    private final Klinik klinik;
    private final InputHelper input;

    /** Konstruktor default: membuat klinik dan pembaca input. */
    public Main() {
        this.klinik = new Klinik();
        this.input = new InputHelper();
    }

    public static void main(String[] args) {
        new Main().jalankan();
    }

    /** Perulangan menu utama sampai pengguna memilih keluar. */
    public void jalankan() {
        System.out.println("Selamat datang di sistem " + klinik.tampilkanInfo());
        boolean berjalan = true;
        while (berjalan) {
            tampilkanMenuUtama();
            int pilihan = input.bacaInt("Pilih menu: ", 0, 10);
            System.out.println();
            try {
                switch (pilihan) {
                    case 1: menuTambahPasien(); break;
                    case 2: menuTampilkanPasien(); break;
                    case 3: menuTambahDokter(); break;
                    case 4: menuTampilkanDokter(); break;
                    case 5: menuBuatKunjungan(); break;
                    case 6: menuKelolaKunjungan(); break;
                    case 7: menuCariData(); break;
                    case 8: menuOperasiAdt(); break;
                    case 9: System.out.print(klinik.buatLaporan()); break;
                    case 10: menuDataContoh(); break;
                    case 0: berjalan = false; break;
                    default: break;
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                // pelanggaran aturan bisnis: tampilkan pesan, program tetap berjalan
                System.out.println("[GAGAL] " + e.getMessage());
            }
        }
        System.out.println("Terima kasih. Program selesai.");
    }

    private void tampilkanMenuUtama() {
        System.out.println();
        System.out.println("================ MENU KLINIK RAWAT JALAN ================");
        System.out.println(" 1. Tambah pasien");
        System.out.println(" 2. Tampilkan semua pasien");
        System.out.println(" 3. Tambah dokter");
        System.out.println(" 4. Tampilkan semua dokter");
        System.out.println(" 5. Buat kunjungan (pendaftaran berobat)");
        System.out.println(" 6. Kelola kunjungan (tampilkan / selesaikan / batalkan)");
        System.out.println(" 7. Cari data (kode atau nama)");
        System.out.println(" 8. Operasi ADT Sorted List (data pasien)");
        System.out.println(" 9. Laporan ringkas");
        System.out.println("10. Muat data contoh");
        System.out.println(" 0. Keluar");
        System.out.println("=========================================================");
    }

    // ================== MENU 1-4: PASIEN & DOKTER ==================

    private void menuTambahPasien() {
        System.out.println("--- Tambah Pasien ---");
        String kode = input.bacaTeksWajib("Kode pasien (contoh P001): ");
        String nama = input.bacaTeksWajib("Nama pasien     : ");
        int umur = input.bacaInt("Umur (tahun)    : ");
        String alamat = input.bacaBaris("Alamat (boleh kosong)  : ");
        String telepon = input.bacaBaris("No. telepon (boleh kosong): ");
        boolean bpjs = input.bacaYaTidak("Peserta BPJS?");
        Pasien pasien = new Pasien(kode, nama, umur, alamat, telepon, bpjs); // validasi di setter
        klinik.tambahPasien(pasien);
        System.out.println("[OK] Pasien berhasil ditambahkan: " + pasien.getKodePasien() + " - " + pasien.getNama());
    }

    private void menuTampilkanPasien() {
        SortedList<Pasien> daftar = klinik.getDaftarPasien();
        System.out.println("--- Daftar Pasien (" + daftar.size() + "/" + daftar.getKapasitas()
                + ", terurut berdasarkan kode) ---");
        daftar.tampilkan();
    }

    private void menuTambahDokter() {
        System.out.println("--- Tambah Dokter ---");
        String kode = input.bacaTeksWajib("Kode dokter (contoh D001): ");
        String nama = input.bacaTeksWajib("Nama dokter     : ");
        String spesialis = input.bacaBaris("Spesialis (kosong = Umum): ");
        double tarif = input.bacaDouble("Tarif konsultasi: ");
        Dokter dokter = new Dokter(kode, nama, spesialis, tarif);
        klinik.tambahDokter(dokter);
        System.out.println("[OK] Dokter berhasil ditambahkan: " + dokter.getKodeDokter() + " - " + dokter.getNama());
    }

    private void menuTampilkanDokter() {
        SortedList<Dokter> daftar = klinik.getDaftarDokter();
        System.out.println("--- Daftar Dokter (" + daftar.size() + "/" + daftar.getKapasitas() + ") ---");
        daftar.tampilkan();
        if (!daftar.isEmpty() && input.bacaYaTidak("Ubah status aktif/nonaktif dokter?")) {
            String kode = input.bacaTeksWajib("Kode dokter: ");
            boolean aktif = input.bacaYaTidak("Jadikan AKTIF? (t = nonaktif)");
            klinik.ubahStatusDokter(kode, aktif);
            System.out.println("[OK] Status dokter " + kode.toUpperCase() + " diubah menjadi "
                    + (aktif ? "Aktif" : "Nonaktif"));
        }
    }

    // ================== MENU 5-6: KUNJUNGAN ==================

    private void menuBuatKunjungan() {
        System.out.println("--- Buat Kunjungan ---");
        if (klinik.getDaftarPasien().isEmpty() || klinik.getDaftarDokter().isEmpty()) {
            System.out.println("[GAGAL] Minimal harus ada 1 pasien dan 1 dokter terlebih dahulu.");
            return;
        }
        String kodePasien = input.bacaTeksWajib("Kode pasien : ").toUpperCase();
        String kodeDokter = input.bacaTeksWajib("Kode dokter : ").toUpperCase();
        LocalDate tanggal = input.bacaTanggal("Tanggal");
        String keluhan = input.bacaTeksWajib("Keluhan     : ");
        Kunjungan k = klinik.buatKunjungan(kodePasien, kodeDokter, tanggal, keluhan);
        int sisa = k.getDokter().hitungSisaKuota(klinik.hitungKunjunganDokter(k.getDokter(), tanggal));
        System.out.println("[OK] Kunjungan " + k.getKodeKunjungan() + " dibuat. Biaya konsultasi: "
                + Klinik.formatRupiah(k.hitungBiayaKonsultasi())
                + " | Sisa kuota " + k.getDokter().getNama() + " hari itu: " + sisa);
    }

    private void menuKelolaKunjungan() {
        System.out.println("--- Kelola Kunjungan ---");
        System.out.println("1. Tampilkan semua kunjungan");
        System.out.println("2. Selesaikan kunjungan (input biaya obat)");
        System.out.println("3. Batalkan kunjungan");
        System.out.println("0. Kembali");
        int pilihan = input.bacaInt("Pilih: ", 0, 3);
        switch (pilihan) {
            case 1:
                SortedList<Kunjungan> daftar = klinik.getDaftarKunjungan();
                System.out.println("Kunjungan (" + daftar.size() + "/" + daftar.getKapasitas() + "):");
                daftar.tampilkan();
                break;
            case 2: {
                String kode = input.bacaTeksWajib("Kode kunjungan: ").toUpperCase();
                double biayaObat = input.bacaDouble("Biaya obat    : ");
                Kunjungan k = klinik.selesaikanKunjungan(kode, biayaObat);
                System.out.println("[OK] " + k.getKodeKunjungan() + " SELESAI. Konsultasi "
                        + Klinik.formatRupiah(k.hitungBiayaKonsultasi()) + " + obat "
                        + Klinik.formatRupiah(k.getBiayaObat()) + " = " + Klinik.formatRupiah(k.hitungTotalBiaya()));
                break;
            }
            case 3: {
                String kode = input.bacaTeksWajib("Kode kunjungan: ").toUpperCase();
                Kunjungan k = klinik.batalkanKunjungan(kode);
                System.out.println("[OK] Kunjungan " + k.getKodeKunjungan() + " dibatalkan.");
                break;
            }
            default:
                break;
        }
    }

    // ================== MENU 7: CARI ==================

    private void menuCariData() {
        System.out.println("--- Cari Data ---");
        System.out.println("1. Cari pasien berdasarkan kode");
        System.out.println("2. Cari pasien berdasarkan nama");
        System.out.println("3. Cari dokter berdasarkan kode");
        System.out.println("4. Cari dokter berdasarkan nama/spesialis");
        System.out.println("5. Cari kunjungan berdasarkan kode");
        System.out.println("0. Kembali");
        int pilihan = input.bacaInt("Pilih: ", 0, 5);
        switch (pilihan) {
            case 1: {
                Pasien p = klinik.cariPasien(input.bacaTeksWajib("Kode pasien: "));
                System.out.println(p == null ? "[INFO] Pasien tidak ditemukan." : "Ditemukan: " + p);
                break;
            }
            case 2: {
                List<Pasien> hasil = klinik.cariPasienByNama(input.bacaTeksWajib("Nama (sebagian): "));
                tampilkanHasilCari(hasil);
                break;
            }
            case 3: {
                Dokter d = klinik.cariDokter(input.bacaTeksWajib("Kode dokter: "));
                System.out.println(d == null ? "[INFO] Dokter tidak ditemukan." : "Ditemukan: " + d);
                break;
            }
            case 4: {
                List<Dokter> hasil = klinik.cariDokterByNama(input.bacaTeksWajib("Nama/spesialis (sebagian): "));
                tampilkanHasilCari(hasil);
                break;
            }
            case 5: {
                Kunjungan k = klinik.cariKunjungan(input.bacaTeksWajib("Kode kunjungan: "));
                System.out.println(k == null ? "[INFO] Kunjungan tidak ditemukan." : "Ditemukan: " + k);
                break;
            }
            default:
                break;
        }
    }

    private void tampilkanHasilCari(List<?> hasil) {
        if (hasil.isEmpty()) {
            System.out.println("[INFO] Tidak ada data yang cocok.");
            return;
        }
        System.out.println("Ditemukan " + hasil.size() + " data:");
        for (Object o : hasil) {
            System.out.println("  " + o);
        }
    }

    // ================== MENU 8: OPERASI ADT ==================

    private void menuOperasiAdt() {
        SortedList<Pasien> list = klinik.getDaftarPasien();
        System.out.println("--- Operasi ADT Sorted List (daftar pasien, kunci = kode pasien) ---");
        System.out.println("1. insert   - tambah pasien (otomatis masuk posisi terurut)");
        System.out.println("2. remove   - hapus pasien berdasarkan kode");
        System.out.println("3. search   - cari index pasien berdasarkan kode (binary search)");
        System.out.println("4. get      - ambil pasien pada index tertentu");
        System.out.println("5. tampilkan- tampilkan isi list");
        System.out.println("6. size     - jumlah data, status kosong/penuh");
        System.out.println("0. Kembali");
        int pilihan = input.bacaInt("Pilih: ", 0, 6);
        switch (pilihan) {
            case 1:
                menuTambahPasien();
                break;
            case 2: {
                Pasien dihapus = klinik.hapusPasien(input.bacaTeksWajib("Kode pasien yang dihapus: ").toUpperCase());
                System.out.println("[OK] remove berhasil: " + dihapus.getKodePasien() + " - " + dihapus.getNama());
                break;
            }
            case 3: {
                if (list.isEmpty()) {
                    System.out.println("[INFO] List KOSONG, tidak ada yang bisa dicari.");
                    break;
                }
                String kode = input.bacaTeksWajib("Kode pasien: ");
                int index = list.search(kode);
                System.out.println(index == -1 ? "[INFO] search: kode " + kode.toUpperCase() + " tidak ditemukan (-1)."
                        : "[OK] search: ditemukan pada index " + index + " -> " + list.get(index));
                break;
            }
            case 4: {
                if (list.isEmpty()) {
                    System.out.println("[INFO] List KOSONG, tidak ada data pada index mana pun.");
                    break;
                }
                int index = input.bacaInt("Index (0 - " + (list.size() - 1) + "): ");
                Pasien p = list.get(index);
                System.out.println(p == null ? "[INFO] get: index " + index + " di luar jangkauan."
                        : "[OK] get(" + index + ") -> " + p);
                break;
            }
            case 5:
                list.tampilkan();
                break;
            case 6:
                System.out.println("size = " + list.size() + " dari kapasitas " + list.getKapasitas()
                        + " | isEmpty = " + list.isEmpty() + " | isFull = " + list.isFull());
                break;
            default:
                break;
        }
    }

    // ================== MENU 10: DATA CONTOH ==================

    private void menuDataContoh() {
        int jumlah = klinik.muatDataContoh();
        System.out.println("[OK] " + jumlah + " data contoh ditambahkan (data dengan kode yang sudah ada dilewati).");
    }
}
