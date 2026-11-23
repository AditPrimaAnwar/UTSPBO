package klinik.adt;

/**
 * Kontrak untuk objek yang bisa disimpan di {@link SortedList}.
 * Setiap objek wajib punya satu kunci (String) yang dipakai untuk
 * mengurutkan dan mencari data.
 */
public interface MemilikiKunci {

    /**
     * @return kunci unik objek, misalnya kode pasien "P001"
     */
    String getKunci();
}
