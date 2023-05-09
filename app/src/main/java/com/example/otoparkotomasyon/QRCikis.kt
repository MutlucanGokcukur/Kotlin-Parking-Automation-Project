package com.example.otoparkotomasyon

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import java.time.LocalDateTime

class QRCikis : AppCompatActivity() {
    private var ID:Int=0
    private lateinit var codeScanner: CodeScanner
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcikis)
        val scannerView=findViewById<CodeScannerView>(R.id.qr_scanner2)
        ID=intent.getIntExtra("ID",0)
        if(checkSelfPermission(android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            val permissionlist= arrayOf(Manifest.permission.CAMERA)
            requestPermissions(permissionlist,1)
        }
        //https://www.youtube.com/watch?v=6OFniVVzmgQ
        codeScanner= CodeScanner(this,scannerView)
        codeScanner.camera= CodeScanner.CAMERA_BACK
        codeScanner.formats= CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode= AutoFocusMode.SAFE
        codeScanner.isAutoFocusEnabled=true
        codeScanner.isFlashEnabled=false
        codeScanner.decodeCallback= DecodeCallback{
            runOnUiThread{
                //Okutma
                val QRKod=it.text.split("\n")
                //Otopark_ID=1
                //Park Edilen Blok:A
                //Blok Numarası:5

                val otoparkid=QRKod[0].get(QRKod[0].lastIndex)      //Son değeri alma (1)
                var blok=QRKod[1].get(QRKod[1].lastIndex)           //Son değeri alma (A)
                var numara=QRKod[2].get(QRKod[2].lastIndex)         //Son değeri alma (5)
                try
                {
                    val Database = openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE, null)
                    val cursor=Database.rawQuery("SELECT * FROM park_halindeki_araclar INNER JOIN araclar ON araclar.Arac_Plaka=park_halindeki_araclar.Arac_Plaka WHERE araclar.Musteri_ID=?", arrayOf(ID.toString()))
                    var okunanblok=""
                    var okunanno=0
                    var okunanotoid=0
                    var aracplaka=""
                    if(cursor.count>0)
                    {
                        while (cursor.moveToNext())
                        {
                            okunanblok=cursor.getString(cursor.getColumnIndex("Arac_Blok").toInt())
                            okunanno=cursor.getInt(cursor.getColumnIndex("Blok_Numara").toInt())
                            okunanotoid=cursor.getInt(cursor.getColumnIndex("Otopark_ID").toInt())
                            aracplaka=cursor.getString(cursor.getColumnIndex("Arac_Plaka").toInt())
                        }
                    }
                    if(okunanotoid.toString()==otoparkid.toString()&&blok.toString()==okunanblok.toString()&&okunanno.toString()==numara.toString())
                    {
                        var ToplamTutar=0
                        val girisbilgiler=Database.rawQuery("SELECT * FROM park_halindeki_araclar INNER JOIN araclar ON araclar.Arac_Plaka=park_halindeki_araclar.Arac_Plaka WHERE araclar.Musteri_ID=?", arrayOf(ID.toString()))
                        val ucretcursor=Database.rawQuery("SELECT Saatlik_Ucret,Gunluk_Ucret,Aylık_Ucret FROM ucretlendirme INNER JOIN araclar on araclar.Arac_Sınıf=ucretlendirme.Arac_Sınıf WHERE Musteri_ID=?", arrayOf(ID.toString()))
                        var saatlik=0
                        var gunluk=0
                        var aylik=0

                        if(ucretcursor.count>0)
                        {
                            while(ucretcursor.moveToNext())
                            {
                                saatlik=ucretcursor.getInt(ucretcursor.getColumnIndex("Saatlik_Ucret").toInt())
                                gunluk=ucretcursor.getInt(ucretcursor.getColumnIndex("Gunluk_Ucret").toInt())
                                aylik=ucretcursor.getInt(ucretcursor.getColumnIndex("Aylık_Ucret").toInt())
                            }
                        }
                        var girissaat=""
                        var giristarih=""
                        if(girisbilgiler.count>0)
                        {
                            while (girisbilgiler.moveToNext())
                            {
                                girissaat=girisbilgiler.getString(girisbilgiler.getColumnIndex("Giris_Saat").toInt())
                                giristarih=girisbilgiler.getString(girisbilgiler.getColumnIndex("Giris_Tarih").toInt()).toString()
                            }
                        }
                        girissaat=girissaat.split(":")[0]

                        val gunceltarih=LocalDateTime.now().toLocalDate().toString().split("-")
                        val Saat=LocalDateTime.now()
                        var guncelsaat=Saat.hour

                        val guncelay=gunceltarih[1]
                        val guncelgun=gunceltarih[2]

                        //ChatGPT
                        val (girisyil,girisay,girisgun)=giristarih.split("-").map { it.toInt() }

                        if(guncelay.toInt()-girisay==0)
                        {
                            if(guncelgun.toInt()-girisgun==0)
                            {
                                ToplamTutar=(guncelsaat-girissaat.toInt())*saatlik
                            }
                            else
                            {
                                ToplamTutar=(guncelgun.toInt()-girisgun)*gunluk
                            }
                        }
                        else
                        {
                            ToplamTutar=(guncelay.toInt()-girisay)*aylik
                        }
                        val alert=AlertDialog.Builder(this)
                        alert.setTitle("Emin misiniz?")
                        alert.setMessage("Çıkış yapılıyor\nToplam tutar:${ToplamTutar}")
                        alert.setPositiveButton("Evet"){dialog,which->
                            Database.execSQL("CREATE TABLE IF NOT EXISTS odenecekler(Musteri_ID INTEGER PRIMARY KEY,Arac_Plaka VARCHAR,Toplam_Tutar INT,FOREIGN KEY(Musteri_ID) REFERENCES Musteriler(Musteri_ID))")
                            Database.execSQL("INSERT INTO odenecekler VALUES(?,?,?)", arrayOf(ID,aracplaka,ToplamTutar))
                            Database.execSQL("DELETE FROM park_halindeki_araclar WHERE Arac_Plaka=?", arrayOf(aracplaka))
                            Toast.makeText(applicationContext,"Ödemenizi görevli personele yapıp çıkabilirsiniz\nİyi günler dileriz",Toast.LENGTH_LONG).show()
                            val intent=Intent(applicationContext,UyeAnaSayfa::class.java)
                            intent.putExtra("ID",ID)
                            startActivity(intent)
                            finish()
                        }
                        alert.setNegativeButton("Hayır"){dialog,which->}
                        alert.show()
                    }
                    else
                    {
                        Toast.makeText(applicationContext,"Buraya giriş yapmış araba bulunamadı\nPersonel ile iletişime geçiniz",Toast.LENGTH_LONG).show()
                        val intent=Intent(applicationContext,UyeAnaSayfa::class.java)
                        intent.putExtra("ID",ID)
                        startActivity(intent)
                        finish()
                    }
                }
                catch (e:java.lang.Exception)
                {
                    Toast.makeText(applicationContext,"HATA",Toast.LENGTH_LONG).show()
                }
            }
        }
        codeScanner.errorCallback= ErrorCallback {
            runOnUiThread{
                Toast.makeText(this,"Hata", Toast.LENGTH_LONG).show()
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume()
    {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}