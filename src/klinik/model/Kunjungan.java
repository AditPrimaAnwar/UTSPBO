package klinik.model;

import java.time.LocalDate;

import klinik.adt.MemilikiKunci;

/**
 * Kunjungan (pendaftaran berobat) yang menghubungkan satu Pasien dengan satu Dokter
 * pada tanggal tertentu. Kunjungan dibuat dan dimiliki oleh Klinik (komposisi).
 *
 * Status kunjungan: MENUNGGU -> SELESAI atau MENUNGGU -> BATAL.
 * Biaya total = biaya konsultasi setelah potongan + biaya obat.
 */
public class Kunjungan implements MemilikiKunci {

    private static final String STATUS_MENUNGGU = "MENUNGGU";
    private static final String STATUS_SELESAI = "SELESAI";
    private static final String STATUS_BATAL = "BATAL";
    private static final String POLA_KODE = "K\\d{4}";

    private String kodeKunjungan;
    private Pasien pasien;
    private Dokter dokter;
    private LocalDate tanggal;
    private String keluhan;
    private double biayaObat;
    private String status;

    /** Konstruktor default: kunjungan kosong berstatus MENUNGGU pada hari ini. */
    public Kunjungan() {
        this.kodeKunjungan = "K0000";
        this.pasien = new Pasien();
        this.dokter = new Dokter();
        this.tanggal = LocalDate.now();
        this.keluhan = "-";
        this.biayaObat = 0;
        this.status = STATUS_MENUNGGU;
    }

    /** Konstruktor berparameter; kunjungan baru selalu berstatus MENUNGGU dan biaya obat 0. */
    public Kunjungan(String kodeKunjungan, Pasien pasien, Dokter dokter, LocalDate tanggal, String keluhan) {
        this.setKodeKunjungan(kodeKunjungan);
        this.setPasien(pasien);
        this.setDokter(dokter);
        this.setTanggal(tanggal);
        this.setKeluhan(keluhan);
        this.biayaObat = 0;
        this.status = STATUS_MENUNGGU;
    }

    // ---------- method perilaku ----------

    /** @return tarif dokter dikurangi potongan pasien (BPJS / lansia) */
    public double hitungBiayaKonsultasi() {
        return dokter.getTarifKonsultasi() * (1 - pasien.hitungPersenPotongan());
    }

    /** @return biaya konsultasi + biaya obat; kunjungan BATAL bernilai 0 */
    public double hitungTotalBiaya() {
        if (isBatal()) {
            return 0;
        }
        return hitungBiayaKonsultasi() + biayaObat;
    }

    /**
     * Menyelesaikan kunjungan dan mencatat biaya obat.
     * Hanya bisa dilakukan jika status masih MENUNGGU.
     */
    public void selesaikan(double biayaObat) {
        if (!isMenunggu()) {
            throw new IllegalStateException("Kunjungan " + kodeKunjungan + " berstatus " + status
                    + ", tidak bisa diselesaikan");
        }
        this.setBiayaObat(biayaObat);
        this.status = STATUS_SELESAI;
    }

    /** Membatalkan kunjungan. Hanya bisa dilakukan jika status masih MENUNGGU. */
    public void batalkan() {
        if (!isMenunggu()) {
            throw new IllegalStateException("Kunjungan " + kodeKunjungan + " berstatus " + status
                    + ", tidak bisa dibatalkan");
        }
        this.status = STATUS_BATAL;
    }

    public boolean isMenunggu() {
        return STATUS_MENUNGGU.equals(status);
    }

    public boolean isSelesai() {
        return STATUS_SELESAI.equals(status);
    }

    public boolean isBatal() {
        return STATUS_BATAL.equals(status);
    }

    /** @return true jika kunjungan ini milik dokter tertentu pada tanggal tertentu dan tidak dibatalkan */
    public boolean isAktifUntuk(Dokter dokter, LocalDate tanggal) {
        return !isBatal() && this.dokter == dokter && this.tanggal.equals(tanggal);
    }

    /** @return informasi kunjungan dalam satu baris */
    public String tampilkanInfo() {
        return kodeKunjungan + " | " + tanggal
                + " | " + pasien.getKodePasien() + " " + pasien.getNama()
                + " -> " + dokter.getKodeDokter() + " " + dokter.getNama()
                + " | Keluhan: " + keluhan + " | " + status
                + " | Total: " + Klinik.formatRupiah(hitungTotalBiaya());
    }

    @Override
    public String getKunci() {
        return kodeKunjungan;
    }

    @Override
    public String toString() {
        return tampilkanInfo();
    }

    // ---------- getter & setter ----------

    public String getKodeKunjungan() {
        return kodeKunjungan;
    }

    public void setKodeKunjungan(String kodeKunjungan) {
        if (kodeKunjungan == null || !kodeKunjungan.trim().toUpperCase().matches(POLA_KODE)) {
            throw new IllegalArgumentException("Kode kunjungan harus berformat K + 4 angka, contoh: K0001");
        }
        this.kodeKunjungan = kodeKunjungan.trim().toUpperCase();
    }

    public Pasien getPasien() {
        return pasien;
    }

    public void setPasien(Pasien pasien) {
        if (pasien == null) {
            throw new IllegalArgumentException("Kunjungan wajib memiliki pasien");
        }
        this.pasien = pasien;
    }

    public Dokter getDokter() {
        return dokter;
    }

    public void setDokter(Dokter dokter) {
        if (dokter == null) {
            throw new IllegalArgumentException("Kunjungan wajib memiliki dokter");
        }
        this.dokter = dokter;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        if (tanggal == null) {
            throw new IllegalArgumentException("Tanggal kunjungan tidak boleh kosong");
        }
        this.tanggal = tanggal;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public void setKeluhan(String keluhan) {
        if (keluhan == null || keluhan.isBlank()) {
            throw new IllegalArgumentException("Keluhan pasien tidak boleh kosong");
        }
        this.keluhan = keluhan.trim();
    }

    public double getBiayaObat() {
        return biayaObat;
    }

    /** Biaya obat tidak boleh negatif (boleh 0 jika tanpa obat). */
    public void setBiayaObat(double biayaObat) {
        if (biayaObat < 0) {
            throw new IllegalArgumentException("Biaya obat tidak boleh negatif");
        }
        this.biayaObat = biayaObat;
    }

    /** Hanya getter: status diubah lewat selesaikan() atau batalkan(). */
    public String getStatus() {
        return status;
    }
}
