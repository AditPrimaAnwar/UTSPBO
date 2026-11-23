package klinik.model;

import klinik.adt.MemilikiKunci;

/**
 * Pasien rawat jalan yang terdaftar di klinik.
 * Kunci pengurutan di SortedList adalah kodePasien (format P001).
 * Aturan bisnis: umur pasien harus 0 - 120 tahun.
 */
public class Pasien implements MemilikiKunci {

    private static final int UMUR_MIN = 0;
    private static final int UMUR_MAX = 120;
    private static final int UMUR_LANSIA = 60;
    private static final double POTONGAN_BPJS = 0.50;
    private static final double POTONGAN_LANSIA = 0.20;
    private static final String POLA_KODE = "P\\d{3}";
    private static final String POLA_TELEPON = "\\+?\\d{8,15}";

    private String kodePasien;
    private String nama;
    private int umur;
    private String alamat;
    private String noTelepon;
    private boolean pesertaBpjs;
    private int jumlahKunjungan;

    /** Konstruktor default: mengisi nilai awal yang aman. */
    public Pasien() {
        this.kodePasien = "P000";
        this.nama = "Tanpa Nama";
        this.umur = 0;
        this.alamat = "-";
        this.noTelepon = "-";
        this.pesertaBpjs = false;
        this.jumlahKunjungan = 0;
    }

    /**
     * Konstruktor berparameter (overloading) hanya dengan data wajib.
     * Alamat dan telepon diisi "-", status BPJS false.
     */
    public Pasien(String kodePasien, String nama, int umur) {
        this(kodePasien, nama, umur, "-", "-", false);
    }

    /**
     * Konstruktor berparameter lengkap. Semua nilai dimasukkan lewat setter
     * agar ikut divalidasi; jika ada yang tidak valid akan dilempar
     * IllegalArgumentException dan objek tidak jadi dibuat.
     */
    public Pasien(String kodePasien, String nama, int umur, String alamat,
                  String noTelepon, boolean pesertaBpjs) {
        this.setKodePasien(kodePasien);
        this.setNama(nama);
        this.setUmur(umur);
        this.setAlamat(alamat);
        this.setNoTelepon(noTelepon);
        this.setPesertaBpjs(pesertaBpjs);
        this.jumlahKunjungan = 0;
    }

    // ---------- method perilaku ----------

    /** @return kategori umur: Anak (0-11), Remaja (12-17), Dewasa (18-59), Lansia (60+) */
    public String getKategoriUmur() {
        if (umur < 12) {
            return "Anak";
        } else if (umur < 18) {
            return "Remaja";
        } else if (umur < UMUR_LANSIA) {
            return "Dewasa";
        }
        return "Lansia";
    }

    /** @return true jika umur pasien 60 tahun ke atas */
    public boolean isLansia() {
        return umur >= UMUR_LANSIA;
    }

    /**
     * Menentukan persen potongan biaya konsultasi.
     * Peserta BPJS dapat 50%, lansia non-BPJS dapat 20%, selain itu 0%.
     * @return nilai potongan dalam bentuk desimal (0.5 = 50%)
     */
    public double hitungPersenPotongan() {
        if (pesertaBpjs) {
            return POTONGAN_BPJS;
        }
        if (isLansia()) {
            return POTONGAN_LANSIA;
        }
        return 0.0;
    }

    /** Menambah hitungan kunjungan pasien; dipanggil klinik saat kunjungan dibuat. */
    public void tambahKunjungan() {
        this.jumlahKunjungan++;
    }

    /** @return informasi pasien dalam satu baris */
    public String tampilkanInfo() {
        return String.format("%s | %-22s | %3d th (%-6s) | BPJS: %-5s | Telp: %-13s | Alamat: %s | Kunjungan: %d",
                kodePasien, nama, umur, getKategoriUmur(), pesertaBpjs ? "Ya" : "Tidak",
                noTelepon, alamat, jumlahKunjungan);
    }

    @Override
    public String getKunci() {
        return kodePasien;
    }

    @Override
    public String toString() {
        return tampilkanInfo();
    }

    // ---------- getter & setter ----------

    public String getKodePasien() {
        return kodePasien;
    }

    /** Kode wajib berformat P + 3 angka, misalnya P001. Otomatis diubah ke huruf besar. */
    public void setKodePasien(String kodePasien) {
        if (kodePasien == null || !kodePasien.trim().toUpperCase().matches(POLA_KODE)) {
            throw new IllegalArgumentException("Kode pasien harus berformat P + 3 angka, contoh: P001");
        }
        this.kodePasien = kodePasien.trim().toUpperCase();
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama == null || nama.isBlank()) {
            throw new IllegalArgumentException("Nama pasien tidak boleh kosong");
        }
        this.nama = nama.trim();
    }

    public int getUmur() {
        return umur;
    }

    /** Aturan bisnis: umur pasien harus berada di rentang 0 - 120 tahun. */
    public void setUmur(int umur) {
        if (umur < UMUR_MIN || umur > UMUR_MAX) {
            throw new IllegalArgumentException("Umur pasien harus " + UMUR_MIN + " - " + UMUR_MAX
                    + " tahun (input: " + umur + ")");
        }
        this.umur = umur;
    }

    public String getAlamat() {
        return alamat;
    }

    /** Alamat boleh kosong; jika kosong disimpan sebagai "-". */
    public void setAlamat(String alamat) {
        this.alamat = (alamat == null || alamat.isBlank()) ? "-" : alamat.trim();
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    /** Nomor telepon boleh kosong ("-"); jika diisi harus 8-15 digit angka, boleh diawali "+". */
    public void setNoTelepon(String noTelepon) {
        if (noTelepon == null || noTelepon.isBlank() || noTelepon.trim().equals("-")) {
            this.noTelepon = "-";
            return;
        }
        String bersih = noTelepon.trim().replace(" ", "").replace("-", "");
        if (!bersih.matches(POLA_TELEPON)) {
            throw new IllegalArgumentException("Nomor telepon harus 8-15 digit angka, contoh: 081234567890");
        }
        this.noTelepon = bersih;
    }

    public boolean isPesertaBpjs() {
        return pesertaBpjs;
    }

    public void setPesertaBpjs(boolean pesertaBpjs) {
        this.pesertaBpjs = pesertaBpjs;
    }

    /** Hanya getter: jumlah kunjungan diubah lewat method tambahKunjungan(). */
    public int getJumlahKunjungan() {
        return jumlahKunjungan;
    }
}
