package klinik.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import klinik.adt.SortedList;

/**
 * Klinik rawat jalan: pusat pengelolaan data pasien, dokter, dan kunjungan.
 *
 * Relasi:
 * - Agregasi dengan Pasien dan Dokter (objek dibuat di luar lalu didaftarkan ke klinik).
 * - Komposisi dengan Kunjungan (kunjungan hanya dibuat oleh klinik lewat buatKunjungan).
 * Semua data disimpan di ADT SortedList sehingga selalu terurut berdasarkan kode.
 *
 * Pelanggaran aturan bisnis dilaporkan dengan IllegalArgumentException /
 * IllegalStateException berisi pesan yang jelas, untuk ditampilkan oleh menu.
 */
public class Klinik {

    private static final int KAPASITAS_PASIEN = 10;
    private static final int KAPASITAS_DOKTER = 5;
    private static final int KAPASITAS_KUNJUNGAN = 30;
    private static final Locale LOCALE_ID = Locale.forLanguageTag("id-ID");

    private String nama;
    private String alamat;
    private String noTelepon;
    private SortedList<Pasien> daftarPasien;
    private SortedList<Dokter> daftarDokter;
    private SortedList<Kunjungan> daftarKunjungan;
    private int nomorKunjunganBerikutnya;

    /** Konstruktor default. */
    public Klinik() {
        this("Klinik Sehat Sentosa", "Jl. Ahmad Yani No. 10, Kendari", "0401123456");
    }

    /** Konstruktor berparameter: membuat klinik beserta list kosong untuk setiap data. */
    public Klinik(String nama, String alamat, String noTelepon) {
        this.setNama(nama);
        this.setAlamat(alamat);
        this.setNoTelepon(noTelepon);
        this.daftarPasien = new SortedList<>(KAPASITAS_PASIEN);
        this.daftarDokter = new SortedList<>(KAPASITAS_DOKTER);
        this.daftarKunjungan = new SortedList<>(KAPASITAS_KUNJUNGAN);
        this.nomorKunjunganBerikutnya = 1;
    }

    // ================== PASIEN ==================

    /** Mendaftarkan pasien baru. Gagal jika list penuh atau kode sudah dipakai. */
    public void tambahPasien(Pasien pasien) {
        if (pasien == null) {
            throw new IllegalArgumentException("Data pasien tidak boleh kosong");
        }
        if (daftarPasien.isFull()) {
            throw new IllegalStateException("Daftar pasien PENUH (kapasitas " + daftarPasien.getKapasitas()
                    + "). Hapus pasien lain terlebih dahulu.");
        }
        if (daftarPasien.search(pasien.getKodePasien()) != -1) {
            throw new IllegalArgumentException("Kode pasien " + pasien.getKodePasien() + " sudah terdaftar");
        }
        daftarPasien.insert(pasien);
    }

    /**
     * Menghapus pasien berdasarkan kode.
     * Aturan: pasien yang masih punya kunjungan MENUNGGU tidak boleh dihapus.
     * @return pasien yang dihapus
     */
    public Pasien hapusPasien(String kodePasien) {
        if (daftarPasien.isEmpty()) {
            throw new IllegalStateException("Daftar pasien KOSONG, tidak ada yang bisa dihapus");
        }
        Pasien pasien = cariPasien(kodePasien);
        if (pasien == null) {
            throw new IllegalArgumentException("Pasien dengan kode " + kodePasien + " tidak ditemukan");
        }
        for (int i = 0; i < daftarKunjungan.size(); i++) {
            Kunjungan k = daftarKunjungan.get(i);
            if (k.getPasien() == pasien && k.isMenunggu()) {
                throw new IllegalStateException("Pasien " + pasien.getKodePasien()
                        + " masih punya kunjungan MENUNGGU (" + k.getKodeKunjungan() + "), tidak bisa dihapus");
            }
        }
        return daftarPasien.remove(pasien.getKodePasien());
    }

    /** @return pasien dengan kode tersebut (binary search), atau null jika tidak ada */
    public Pasien cariPasien(String kodePasien) {
        return daftarPasien.get(daftarPasien.search(kodePasien));
    }

    /** @return semua pasien yang namanya mengandung kata kunci (tidak peka huruf besar/kecil) */
    public List<Pasien> cariPasienByNama(String kataKunci) {
        List<Pasien> hasil = new ArrayList<>();
        String kunci = kataKunci == null ? "" : kataKunci.trim().toLowerCase();
        for (int i = 0; i < daftarPasien.size(); i++) {
            Pasien p = daftarPasien.get(i);
            if (p.getNama().toLowerCase().contains(kunci)) {
                hasil.add(p);
            }
        }
        return hasil;
    }

    // ================== DOKTER ==================

    /** Mendaftarkan dokter baru. Gagal jika list penuh atau kode sudah dipakai. */
    public void tambahDokter(Dokter dokter) {
        if (dokter == null) {
            throw new IllegalArgumentException("Data dokter tidak boleh kosong");
        }
        if (daftarDokter.isFull()) {
            throw new IllegalStateException("Daftar dokter PENUH (kapasitas " + daftarDokter.getKapasitas() + ")");
        }
        if (daftarDokter.search(dokter.getKodeDokter()) != -1) {
            throw new IllegalArgumentException("Kode dokter " + dokter.getKodeDokter() + " sudah terdaftar");
        }
        daftarDokter.insert(dokter);
    }

    /** @return dokter dengan kode tersebut, atau null jika tidak ada */
    public Dokter cariDokter(String kodeDokter) {
        return daftarDokter.get(daftarDokter.search(kodeDokter));
    }

    /** @return semua dokter yang nama atau spesialisnya mengandung kata kunci */
    public List<Dokter> cariDokterByNama(String kataKunci) {
        List<Dokter> hasil = new ArrayList<>();
        String kunci = kataKunci == null ? "" : kataKunci.trim().toLowerCase();
        for (int i = 0; i < daftarDokter.size(); i++) {
            Dokter d = daftarDokter.get(i);
            if (d.getNama().toLowerCase().contains(kunci) || d.getSpesialis().toLowerCase().contains(kunci)) {
                hasil.add(d);
            }
        }
        return hasil;
    }

    /** Mengubah status aktif/nonaktif dokter. */
    public void ubahStatusDokter(String kodeDokter, boolean aktif) {
        Dokter dokter = cariDokter(kodeDokter);
        if (dokter == null) {
            throw new IllegalArgumentException("Dokter dengan kode " + kodeDokter + " tidak ditemukan");
        }
        dokter.ubahStatus(aktif);
    }

    // ================== KUNJUNGAN ==================

    /**
     * Membuat kunjungan baru (transaksi utama) yang menghubungkan pasien dan dokter.
     * Aturan bisnis yang dicek:
     * 1. pasien dan dokter harus terdaftar,
     * 2. dokter harus aktif,
     * 3. dokter maksimal 5 kunjungan aktif pada tanggal tersebut,
     * 4. daftar kunjungan belum penuh.
     * @return kunjungan yang baru dibuat
     */
    public Kunjungan buatKunjungan(String kodePasien, String kodeDokter, LocalDate tanggal, String keluhan) {
        Pasien pasien = cariPasien(kodePasien);
        if (pasien == null) {
            throw new IllegalArgumentException("Pasien dengan kode " + kodePasien + " tidak ditemukan");
        }
        Dokter dokter = cariDokter(kodeDokter);
        if (dokter == null) {
            throw new IllegalArgumentException("Dokter dengan kode " + kodeDokter + " tidak ditemukan");
        }
        if (!dokter.isAktif()) {
            throw new IllegalStateException("Dokter " + dokter.getNama() + " sedang NONAKTIF, tidak bisa menerima kunjungan");
        }
        int jumlahHariItu = hitungKunjunganDokter(dokter, tanggal);
        if (!dokter.bisaMenerimaKunjungan(jumlahHariItu)) {
            throw new IllegalStateException("Kuota " + dokter.getNama() + " pada " + tanggal + " sudah penuh ("
                    + jumlahHariItu + "/" + dokter.getMaksKunjunganPerHari() + " kunjungan)");
        }
        if (daftarKunjungan.isFull()) {
            throw new IllegalStateException("Daftar kunjungan PENUH (kapasitas " + daftarKunjungan.getKapasitas() + ")");
        }
        // Komposisi: objek Kunjungan dibuat di dalam Klinik
        Kunjungan kunjungan = new Kunjungan(buatKodeKunjungan(), pasien, dokter, tanggal, keluhan);
        daftarKunjungan.insert(kunjungan);
        nomorKunjunganBerikutnya++;
        pasien.tambahKunjungan();
        return kunjungan;
    }

    /** Menyelesaikan kunjungan dan mencatat biaya obat. */
    public Kunjungan selesaikanKunjungan(String kodeKunjungan, double biayaObat) {
        Kunjungan kunjungan = cariKunjunganWajib(kodeKunjungan);
        kunjungan.selesaikan(biayaObat);
        return kunjungan;
    }

    /** Membatalkan kunjungan; kuota dokter pada hari itu kembali bertambah. */
    public Kunjungan batalkanKunjungan(String kodeKunjungan) {
        Kunjungan kunjungan = cariKunjunganWajib(kodeKunjungan);
        kunjungan.batalkan();
        return kunjungan;
    }

    /** @return kunjungan dengan kode tersebut, atau null jika tidak ada */
    public Kunjungan cariKunjungan(String kodeKunjungan) {
        return daftarKunjungan.get(daftarKunjungan.search(kodeKunjungan));
    }

    /** @return jumlah kunjungan aktif (tidak batal) seorang dokter pada tanggal tertentu */
    public int hitungKunjunganDokter(Dokter dokter, LocalDate tanggal) {
        int jumlah = 0;
        for (int i = 0; i < daftarKunjungan.size(); i++) {
            if (daftarKunjungan.get(i).isAktifUntuk(dokter, tanggal)) {
                jumlah++;
            }
        }
        return jumlah;
    }

    // ================== LAPORAN ==================

    /** @return total biaya dari semua kunjungan berstatus SELESAI */
    public double hitungTotalPendapatan() {
        double total = 0;
        for (int i = 0; i < daftarKunjungan.size(); i++) {
            Kunjungan k = daftarKunjungan.get(i);
            if (k.isSelesai()) {
                total += k.hitungTotalBiaya();
            }
        }
        return total;
    }

    /** @return laporan ringkas klinik dalam bentuk teks */
    public String buatLaporan() {
        int bpjs = 0;
        int lansia = 0;
        for (int i = 0; i < daftarPasien.size(); i++) {
            Pasien p = daftarPasien.get(i);
            if (p.isPesertaBpjs()) {
                bpjs++;
            }
            if (p.isLansia()) {
                lansia++;
            }
        }
        int dokterAktif = 0;
        for (int i = 0; i < daftarDokter.size(); i++) {
            if (daftarDokter.get(i).isAktif()) {
                dokterAktif++;
            }
        }
        int menunggu = 0;
        int selesai = 0;
        int batal = 0;
        for (int i = 0; i < daftarKunjungan.size(); i++) {
            Kunjungan k = daftarKunjungan.get(i);
            if (k.isMenunggu()) {
                menunggu++;
            } else if (k.isSelesai()) {
                selesai++;
            } else {
                batal++;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("===== LAPORAN RINGKAS ").append(nama.toUpperCase()).append(" =====\n");
        sb.append(String.format("Jumlah pasien    : %d / %d (BPJS: %d, Lansia: %d)%n",
                daftarPasien.size(), daftarPasien.getKapasitas(), bpjs, lansia));
        sb.append(String.format("Jumlah dokter    : %d / %d (Aktif: %d)%n",
                daftarDokter.size(), daftarDokter.getKapasitas(), dokterAktif));
        sb.append(String.format("Jumlah kunjungan : %d / %d (Menunggu: %d, Selesai: %d, Batal: %d)%n",
                daftarKunjungan.size(), daftarKunjungan.getKapasitas(), menunggu, selesai, batal));
        sb.append("Total pendapatan : ").append(formatRupiah(hitungTotalPendapatan()))
                .append(" (dari kunjungan SELESAI)\n");
        sb.append("Kunjungan per dokter (tanpa yang batal):\n");
        if (daftarDokter.isEmpty()) {
            sb.append("  (belum ada dokter)\n");
        }
        for (int i = 0; i < daftarDokter.size(); i++) {
            Dokter d = daftarDokter.get(i);
            int jumlah = 0;
            for (int j = 0; j < daftarKunjungan.size(); j++) {
                Kunjungan k = daftarKunjungan.get(j);
                if (k.getDokter() == d && !k.isBatal()) {
                    jumlah++;
                }
            }
            sb.append(String.format("  %s %-22s : %d kunjungan%n", d.getKodeDokter(), d.getNama(), jumlah));
        }
        return sb.toString();
    }

    /** @return identitas klinik dalam satu baris */
    public String tampilkanInfo() {
        return nama + " | " + alamat + " | Telp: " + noTelepon;
    }

    /**
     * Mengisi data contoh untuk keperluan demo. Data yang kodenya sudah ada dilewati.
     * @return jumlah data yang berhasil ditambahkan
     */
    public int muatDataContoh() {
        int berhasil = 0;
        Dokter[] dokters = {
            new Dokter("D002", "dr. Sari Dewi", "Gigi", 75000),
            new Dokter("D001", "dr. Andi Pratama", "Umum", 50000),
            new Dokter("D003", "dr. Budi Santoso, Sp.A", "Anak", 100000, false)
        };
        Pasien[] pasiens = {
            new Pasien("P003", "Wa Ode Nurhaliza", 25, "Jl. Saranani", "081234567801", false),
            new Pasien("P001", "La Ode Ahmad", 67, "Jl. Ahmad Yani", "081234567802", true),
            new Pasien("P002", "Siti Aminah", 8, "Jl. Malaka", "081234567803", false),
            new Pasien("P005", "Rahmat Hidayat", 72, "Jl. Poros Bandara", "081234567804", false)
        };
        for (Dokter d : dokters) {
            if (!daftarDokter.isFull() && cariDokter(d.getKodeDokter()) == null) {
                daftarDokter.insert(d);
                berhasil++;
            }
        }
        for (Pasien p : pasiens) {
            if (!daftarPasien.isFull() && cariPasien(p.getKodePasien()) == null) {
                daftarPasien.insert(p);
                berhasil++;
            }
        }
        return berhasil;
    }

    /** Format angka menjadi Rupiah, contoh 50000 -> "Rp50.000". */
    public static String formatRupiah(double nilai) {
        return String.format(LOCALE_ID, "Rp%,.0f", nilai);
    }

    // ---------- helper internal ----------

    private String buatKodeKunjungan() {
        return String.format("K%04d", nomorKunjunganBerikutnya);
    }

    private Kunjungan cariKunjunganWajib(String kodeKunjungan) {
        if (daftarKunjungan.isEmpty()) {
            throw new IllegalStateException("Belum ada kunjungan (daftar KOSONG)");
        }
        Kunjungan kunjungan = cariKunjungan(kodeKunjungan);
        if (kunjungan == null) {
            throw new IllegalArgumentException("Kunjungan dengan kode " + kodeKunjungan + " tidak ditemukan");
        }
        return kunjungan;
    }

    // ---------- getter & setter ----------

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama == null || nama.isBlank()) {
            throw new IllegalArgumentException("Nama klinik tidak boleh kosong");
        }
        this.nama = nama.trim();
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        if (alamat == null || alamat.isBlank()) {
            throw new IllegalArgumentException("Alamat klinik tidak boleh kosong");
        }
        this.alamat = alamat.trim();
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        if (noTelepon == null || !noTelepon.trim().matches("\\d{6,15}")) {
            throw new IllegalArgumentException("Nomor telepon klinik harus 6-15 digit angka");
        }
        this.noTelepon = noTelepon.trim();
    }

    /** List pasien (dipakai menu untuk operasi ADT: search, get, tampilkan, size). */
    public SortedList<Pasien> getDaftarPasien() {
        return daftarPasien;
    }

    public SortedList<Dokter> getDaftarDokter() {
        return daftarDokter;
    }

    public SortedList<Kunjungan> getDaftarKunjungan() {
        return daftarKunjungan;
    }
}
