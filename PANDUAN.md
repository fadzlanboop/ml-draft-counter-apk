# Panduan: Ubah ML Draft Counter Jadi Aplikasi Android (APK)

Proyek ini pakai **Capacitor** — alat resmi dari tim Ionic yang membungkus website (folder `www/`) jadi aplikasi Android asli, plus kode native untuk bubble mengambang.

## Yang kamu butuhkan di komputer (bukan di HP)
1. **Node.js** (nodejs.org) — install versi LTS
2. **Android Studio** (developer.android.com/studio) — untuk compile jadi APK
3. Koneksi internet (buat download dependency)

## Langkah 1 — Siapkan project
Buka terminal/command prompt di folder `mlcounter-app` ini, lalu jalankan:

```bash
npm install
npx cap init "ML Draft Counter" "com.oo.mldraftcounter" --web-dir=www
npx cap add android
```

## Langkah 2 — Pasang kode bubble mengambang
Semua file yang perlu ditempel ada di folder `android-native/`. Setelah `npx cap add android` selesai, folder `android/` akan muncul. Pindahkan file-file ini:

| File di android-native/ | Pindahkan ke |
|---|---|
| `OverlayBridgePlugin.java` | `android/app/src/main/java/com/oo/mldraftcounter/` |
| `FloatingBubbleService.kt` | `android/app/src/main/java/com/oo/mldraftcounter/` |
| `MainActivity.java` | **Timpa** file yang sudah ada di path yang sama |
| `AndroidManifest_additions.xml` | **Jangan dipindah langsung** — buka `android/app/src/main/AndroidManifest.xml`, lalu salin isi dari file ini ke tempat yang ditandai (permission di luar `<application>`, service di dalamnya) |

> Karena `FloatingBubbleService.kt` pakai Kotlin, pastikan saat `npx cap add android` Android Studio menawarkan dukungan Kotlin (defaultnya sudah aktif di project Capacitor modern).

## Langkah 3 — Sinkronkan & buka di Android Studio
```bash
npx cap sync
npx cap open android
```
Android Studio akan terbuka otomatis dengan project lengkap.

## Langkah 4 — Build APK
Di Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
File APK hasil jadi ada di `android/app/build/outputs/apk/debug/app-debug.apk` — tinggal kirim ke HP kamu dan install (aktifkan "Izinkan dari sumber tidak dikenal" saat instal pertama kali).

## Cara kerja bubble-nya
1. Saat app pertama dibuka, akan muncul dialog sistem minta izin **"Tampil di atas aplikasi lain"** — izinkan.
2. Di layar utama app, tombol emas **"Aktifkan Bubble Mengambang"** akan muncul (tombol ini otomatis tersembunyi kalau kamu buka lewat browser biasa, bukan APK).
3. Setelah diaktifkan, bubble kecil muncul mengambang, bisa digeser ke mana saja, tetap terlihat walau kamu pindah ke app ML.
4. Setiap kali kamu tap hero musuh di dalam app, bubble otomatis update menampilkan 3 rekomendasi counter teratas.
5. Ketuk bubble untuk buka/tutup panel teksnya.

## Kalau tidak mau ribet coding: pakai PWA Builder
Tanpa install apapun di komputer:
1. Deploy dulu situs kamu (Netlify, sudah kamu punya: `mlcounter.netlify.app`)
2. Buka **pwabuilder.com**, masukkan URL situsmu
3. Klik "Package for Stores" → pilih Android → download APK/AAB siap install

Catatan: cara ini **tidak menyertakan fitur bubble mengambang** (karena itu murni fitur native, bukan fitur PWA) — kalau kamu mau bubble-nya, wajib lewat jalur Capacitor di atas.
