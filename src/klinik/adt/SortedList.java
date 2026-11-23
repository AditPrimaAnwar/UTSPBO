package klinik.adt;

/**
 * ADT Sorted List berbasis array dengan kapasitas tetap.
 * Data selalu terurut naik berdasarkan kunci ({@link MemilikiKunci#getKunci()}),
 * dibandingkan tanpa membedakan huruf besar/kecil. Kunci tidak boleh duplikat.
 *
 * Operasi publik: insert, remove, search, get, tampilkan, size,
 * ditambah isEmpty, isFull, dan getKapasitas.
 * Semua operasi menangani kondisi penuh, kosong, dan data tidak ditemukan
 * tanpa membuat program crash.
 *
 * @param <T> tipe data yang disimpan, wajib mengimplementasikan MemilikiKunci
 */
public class SortedList<T extends MemilikiKunci> {

    private static final int KAPASITAS_DEFAULT = 10;

    private final Object[] data;   // penyimpanan internal (private, tidak bisa diakses dari luar)
    private final int kapasitas;   // jumlah maksimum elemen
    private int jumlah;            // jumlah elemen yang sedang terisi

    /** Konstruktor default: kapasitas 10. */
    public SortedList() {
        this(KAPASITAS_DEFAULT);
    }

    /**
     * Konstruktor berparameter.
     * @param kapasitas jumlah maksimum elemen, harus lebih dari 0
     */
    public SortedList(int kapasitas) {
        if (kapasitas <= 0) {
            throw new IllegalArgumentException("Kapasitas SortedList harus lebih dari 0");
        }
        this.kapasitas = kapasitas;
        this.data = new Object[kapasitas];
        this.jumlah = 0;
    }

    /**
     * Menyisipkan elemen pada posisi yang tepat sehingga list tetap terurut.
     * Cara kerja: mulai dari elemen paling belakang, geser elemen yang kuncinya
     * lebih besar satu langkah ke kanan, lalu taruh elemen baru di celah yang kosong.
     *
     * Prasyarat: list belum penuh, item tidak null, kunci belum ada.
     * @return true jika berhasil; false jika list penuh, item null, atau kunci duplikat
     */
    public boolean insert(T item) {
        if (item == null || isFull()) {
            return false;
        }
        if (search(item.getKunci()) != -1) {
            return false; // kunci sudah ada, tidak boleh duplikat
        }
        int posisi = jumlah;
        while (posisi > 0 && bandingkan(ambil(posisi - 1).getKunci(), item.getKunci()) > 0) {
            data[posisi] = data[posisi - 1];
            posisi--;
        }
        data[posisi] = item;
        jumlah++;
        return true;
    }

    /**
     * Menghapus elemen berdasarkan kunci, lalu menggeser elemen setelahnya ke kiri
     * agar tidak ada celah dan urutan tetap terjaga.
     *
     * @return elemen yang dihapus, atau null jika list kosong / kunci tidak ditemukan
     */
    public T remove(String kunci) {
        int index = search(kunci);
        if (index == -1) {
            return null;
        }
        T dihapus = ambil(index);
        for (int i = index; i < jumlah - 1; i++) {
            data[i] = data[i + 1];
        }
        data[jumlah - 1] = null; // bersihkan slot terakhir
        jumlah--;
        return dihapus;
    }

    /**
     * Mencari posisi elemen dengan binary search (memanfaatkan data yang terurut).
     *
     * @return index elemen (0..size-1), atau -1 jika tidak ditemukan / list kosong
     */
    public int search(String kunci) {
        if (kunci == null || isEmpty()) {
            return -1;
        }
        int kiri = 0;
        int kanan = jumlah - 1;
        while (kiri <= kanan) {
            int tengah = (kiri + kanan) / 2;
            int hasil = bandingkan(ambil(tengah).getKunci(), kunci);
            if (hasil == 0) {
                return tengah;
            } else if (hasil < 0) {
                kiri = tengah + 1;
            } else {
                kanan = tengah - 1;
            }
        }
        return -1;
    }

    /**
     * Mengambil elemen pada index tertentu tanpa menghapusnya.
     *
     * @return elemen pada index, atau null jika index di luar jangkauan
     */
    public T get(int index) {
        if (index < 0 || index >= jumlah) {
            return null;
        }
        return ambil(index);
    }

    /** Menampilkan seluruh isi list beserta index-nya ke layar. */
    public void tampilkan() {
        if (isEmpty()) {
            System.out.println("  (list kosong)");
            return;
        }
        for (int i = 0; i < jumlah; i++) {
            System.out.println("  [" + i + "] " + ambil(i));
        }
    }

    /** @return jumlah elemen yang tersimpan */
    public int size() {
        return jumlah;
    }

    /** @return true jika list tidak berisi elemen */
    public boolean isEmpty() {
        return jumlah == 0;
    }

    /** @return true jika jumlah elemen sudah sama dengan kapasitas */
    public boolean isFull() {
        return jumlah == kapasitas;
    }

    /** @return kapasitas maksimum list */
    public int getKapasitas() {
        return kapasitas;
    }

    // ---------- helper internal ----------

    /** Membandingkan dua kunci tanpa membedakan huruf besar/kecil. */
    private int bandingkan(String a, String b) {
        return a.compareToIgnoreCase(b);
    }

    /** Mengambil elemen array dan mengubahnya ke tipe T (aman karena hanya T yang bisa masuk lewat insert). */
    @SuppressWarnings("unchecked")
    private T ambil(int index) {
        return (T) data[index];
    }
}
