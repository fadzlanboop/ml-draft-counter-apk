# Panduan: Build APK Langsung dari HP (Tanpa Laptop)

Konsepnya: kamu **push kode dari HP ke GitHub**, lalu **server GitHub yang meng-compile APK-nya** (gratis, otomatis, lewat file `.github/workflows/build-apk.yml` yang sudah disiapkan). Kamu tinggal download hasilnya.

## Yang dibutuhkan
- App **Termux** — install dari **F-Droid** (f-droid.org), JANGAN dari Play Store (versi Play Store sudah tidak dikembangkan lagi)
- Akun **GitHub** (gratis, buat di github.com kalau belum punya)
- File project `mlcounter-app` (hasil zip dari saya), sudah diekstrak di folder Download HP kamu

## Langkah 1 — Setup Termux
Buka Termux, jalankan satu-satu:
```bash
pkg update -y
pkg install git -y
termux-setup-storage
```
Saat muncul izin akses penyimpanan, tekan **Izinkan**.

## Langkah 2 — Buat repository kosong di GitHub
1. Buka github.com lewat browser HP, login
2. Tombol **+** di pojok kanan atas → **New repository**
3. Nama: `ml-draft-counter-app`, biarkan **Public** atau **Private** terserah kamu, JANGAN centang "Add README"
4. Klik **Create repository** — catat alamatnya, contoh: `https://github.com/USERNAMEKAMU/ml-draft-counter-app.git`

## Langkah 3 — Buat Personal Access Token (pengganti password)
GitHub tidak menerima password biasa lewat terminal. Buat token:
1. github.com → foto profil → **Settings** → scroll ke **Developer settings** (paling bawah)
2. **Personal access tokens** → **Tokens (classic)** → **Generate new token (classic)**
3. Centang `repo` → **Generate token**
4. **Salin token ini sekarang** (hanya muncul sekali) — simpan sementara di Notes HP

## Langkah 4 — Push project dari Termux
Di Termux:
```bash
cd storage/downloads/mlcounter-app
git init
git add .
git commit -m "Setup awal ML Draft Counter"
git branch -M main
git remote add origin https://github.com/USERNAMEKAMU/ml-draft-counter-app.git
git push -u origin main
```
Saat diminta **username**, isi username GitHub kamu.
Saat diminta **password**, tempel **token** dari Langkah 3 (bukan password akun).

> Kalau folder hasil ekstrak zip tidak persis di `storage/downloads/mlcounter-app`, cek dulu lokasinya dengan `ls storage/downloads/` untuk menyesuaikan nama foldernya.

## Langkah 5 — Lihat proses build otomatis
1. Buka repo kamu di github.com lewat browser
2. Tab **Actions** di atas — akan ada proses "Build APK" sedang/sudah berjalan (durasi sekitar 5–10 menit)
3. Tunggu sampai tanda centang hijau ✅

## Langkah 6 — Download APK hasil jadi
1. Masih di tab **Actions**, klik run yang sudah selesai (centang hijau)
2. Scroll ke bawah ke bagian **Artifacts**
3. Tap **ml-draft-counter-debug-apk** → otomatis terdownload sebagai file `.zip`
4. Ekstrak zip itu di HP → dapat file `app-debug.apk`
5. Install seperti biasa (aktifkan "Izinkan dari sumber tidak dikenal" saat instal pertama kali)

## Kalau build gagal (tanda silang merah ❌)
Wajar terjadi di percobaan pertama — konfigurasi Android agak sensitif terhadap versi. Buka run yang gagal → klik langkah yang bertanda merah → **salin pesan errornya**, lalu kirim ke saya, saya bantu perbaiki filenya. Setelah diperbaiki, tinggal push ulang dan build otomatis jalan lagi.

## Update aplikasi selanjutnya
Setiap kali kamu (atau saya) mengubah kode di `www/index.html` dst, tinggal ulangi dari Langkah 4 (`git add .`, `git commit`, `git push`) — build APK baru otomatis jalan lagi tanpa perlu ulang setup dari nol.
