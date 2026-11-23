# UTS Pemrograman Berorientasi Objek - Klinik Rawat Jalan

| | |
|---|---|
| Nama | Adit Prima Anwar |
| NIM | 22315021 |
| Mata Kuliah | Object Oriented Programming |
| Dosen | Muhammad Avied Bachmid, S.Kom., M.Kom. |
| Topik | Klinik Rawat Jalan (digit terakhir NIM = 1) |
| Jenis ADT | Sorted List / array terurut (digit kedua dari belakang = 2, 2 mod 3 = 2) |

Aplikasi console Java untuk mengelola pasien, dokter, dan kunjungan rawat jalan.
Data disimpan di memori menggunakan ADT **SortedList** buatan sendiri (array terurut berdasarkan kode).

## Aturan bisnis (invariant)

1. Satu dokter maksimal **5 kunjungan per hari** (kunjungan yang dibatalkan tidak dihitung).
2. Umur pasien harus **0 - 120** tahun.
3. Dokter yang nonaktif tidak bisa menerima kunjungan.
4. Tarif konsultasi harus lebih dari 0, biaya obat tidak boleh negatif.
5. Kode pasien/dokter unik (P001, D001), kode kunjungan dibuat otomatis (K0001).
6. Kunjungan hanya bisa diselesaikan/dibatalkan selama statusnya MENUNGGU.
7. Pasien yang masih punya kunjungan MENUNGGU tidak bisa dihapus.
8. Potongan biaya konsultasi: peserta BPJS 50%, pasien lansia (>= 60 th) non-BPJS 20%.

## Struktur folder

```
src/klinik/
  adt/    MemilikiKunci.java, SortedList.java
  model/  Pasien.java, Dokter.java, Kunjungan.java, Klinik.java
  app/    Main.java, InputHelper.java
docs/     class diagram (draw.io), draf laporan, skenario uji
tests/    file input skenario uji + hasil keluarannya
```

## Cara menjalankan

Butuh JDK 11 atau lebih baru.

```bat
run.bat
```

atau manual:

```bat
javac -encoding UTF-8 -d out src\klinik\adt\*.java src\klinik\model\*.java src\klinik\app\*.java
java -cp out klinik.app.Main
```
