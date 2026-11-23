package klinik.model;

import klinik.adt.MemilikiKunci;

/**
 * Dokter yang praktik di klinik.
 * Kunci pengurutan di SortedList adalah kodeDokter (format D001).
 * Aturan bisnis: satu dokter maksimal 5 kunjungan per hari dan
 * dokter nonaktif tidak bisa menerima kunjungan.
 */
public class Dokter implements MemilikiKunci {

    private static final int MAKS_KUNJUNGAN_PER_HARI = 5;
    private static final String POLA_KODE = "D\\d{3}";

    private String kodeDokter;
    private String nama;
    private String spesialis;
    private double tarifKonsultasi;
    private boolean aktif;

    /** Konstruktor default. */
    public Dokter() {
        this.kodeDokter = "D000";
        this.nama = "Tanpa Nama";
        this.spesialis = "Umum";
        this.tarifKonsultasi = 1;
        this.aktif = true;
    }

    /** Konstruktor berparameter (overloading): dokter baru langsung berstatus aktif. */
    public Dokter(String kodeDokter, String nama, String spesialis, double tarifKonsultasi) {
        this(kodeDokter, nama, spesialis, tarifKonsultasi, true);
    }

    /** Konstruktor berparameter lengkap; semua nilai divalidasi lewat setter. */
    public Dokter(String kodeDokter, String nama, String spesialis, double tarifKonsultasi, boolean aktif) {
        this.setKodeDokter(kodeDokter);
        this.setNama(nama);
        this.setSpesialis(spesialis);
        this.setTarifKonsultasi(tarifKonsultasi);
        this.aktif = aktif;
    }

    // ---------- method perilaku ----------

    /**
     * Mengecek apakah dokter masih bisa menerima kunjungan pada suatu hari.
     * @param jumlahKunjunganHariItu kunjungan aktif dokter pada tanggal tersebut
     */
    public boolean bisaMenerimaKunjungan(int jumlahKunjunganHariItu) {
        return aktif && jumlahKunjunganHariItu < MAKS_KUNJUNGAN_PER_HARI;
    }

    /** @return sisa kuota kunjungan pada hari itu (tidak pernah negatif) */
    public int hitungSisaKuota(int jumlahKunjunganHariItu) {
        return Math.max(0, MAKS_KUNJUNGAN_PER_HARI - jumlahKunjunganHariItu);
    }

    /** Mengubah status praktik dokter (aktif / nonaktif). */
    public void ubahStatus(boolean aktif) {
        this.aktif = aktif;
    }

    /** @return batas maksimum kunjungan per hari */
    public int getMaksKunjunganPerHari() {
        return MAKS_KUNJUNGAN_PER_HARI;
    }

    /** @return informasi dokter dalam satu baris */
    public String tampilkanInfo() {
        String statusTeks = "Nonaktif";
        if (aktif) {
            statusTeks = "Aktif";
        }
        return kodeDokter + " | " + nama + " | " + spesialis
                + " | Tarif: " + Klinik.formatRupiah(tarifKonsultasi) + " | Status: " + statusTeks;
    }

    @Override
    public String getKunci() {
        return kodeDokter;
    }

    @Override
    public String toString() {
        return tampilkanInfo();
    }

    // ---------- getter & setter ----------

    public String getKodeDokter() {
        return kodeDokter;
    }

    /** Kode wajib berformat D + 3 angka, misalnya D001. */
    public void setKodeDokter(String kodeDokter) {
        if (kodeDokter == null || !kodeDokter.trim().toUpperCase().matches(POLA_KODE)) {
            throw new IllegalArgumentException("Kode dokter harus berformat D + 3 angka, contoh: D001");
        }
        this.kodeDokter = kodeDokter.trim().toUpperCase();
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama == null || nama.isBlank()) {
            throw new IllegalArgumentException("Nama dokter tidak boleh kosong");
        }
        this.nama = nama.trim();
    }

    public String getSpesialis() {
        return spesialis;
    }

    /** Spesialis boleh kosong; jika kosong dianggap dokter "Umum". */
    public void setSpesialis(String spesialis) {
        if (spesialis == null || spesialis.isBlank()) {
            this.spesialis = "Umum";
        } else {
            this.spesialis = spesialis.trim();
        }
    }

    public double getTarifKonsultasi() {
        return tarifKonsultasi;
    }

    /** Tarif konsultasi harus lebih dari 0. */
    public void setTarifKonsultasi(double tarifKonsultasi) {
        if (tarifKonsultasi <= 0) {
            throw new IllegalArgumentException("Tarif konsultasi harus lebih dari 0");
        }
        this.tarifKonsultasi = tarifKonsultasi;
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }
}
