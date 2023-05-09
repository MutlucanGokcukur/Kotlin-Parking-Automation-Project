package com.example.otoparkotomasyon

import android.content.Intent
import android.database.DatabaseErrorHandler
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.tabs.TabLayout.Tab
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var intent: Intent
    private lateinit var Tablolar:MutableList<String>
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Tablolar= mutableListOf<String>()
        title="Giriş"
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val cursor=Database.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master",null)
        while (cursor.moveToNext())
        {
            Tablolar.add(cursor.getString(cursor.getColumnIndex("tbl_name").toInt()))
        }
        if(!Tablolar.contains("otoparklar"))
        {
            Database.execSQL("CREATE TABLE otoparklar(Otopark_ID INTEGER PRIMARY KEY AUTOINCREMENT,Otopark_Adı VARCHAR,Il VARCHAR,Ilce VARCHAR,Adres VARCHAR,Kapasite INT)")
            Database.execSQL("INSERT INTO otoparklar(Otopark_Adı,Il,Ilce,Adres,Kapasite) VALUES(?,?,?,?,?)",
                arrayOf("Umut Otopark","Kocaeli","İzmit","Umuttepe NO:253",5000))
            Database.execSQL("INSERT INTO otoparklar(Otopark_Adı,Il,Ilce,Adres,Kapasite) VALUES(?,?,?,?,?)",
                arrayOf("Kocaeli Otopark","Kocaeli","Gebze","Gebze NO:124",10000))
        }
        if(!Tablolar.contains("personeller"))
        {
            Database.execSQL("CREATE TABLE personeller(Personel_ID INTEGER PRIMARY KEY AUTOINCREMENT,Otopark_ID INT,Personel_Adı VARCHAR,Personel_Soyadı VARCHAR,Personel_Telefon INT,Personel_EPosta VARCHAR,Personel_Sifre VARCHAR,FOREIGN KEY(Otopark_ID) REFERENCES otoparklar(Otopark_ID))")
            Database.execSQL("INSERT INTO personeller(Otopark_ID,Personel_Adı,Personel_Soyadı,Personel_Telefon,Personel_EPosta,Personel_Sifre) VALUES(?,?,?,?,?,?)",
                arrayOf(1,"Mutlucan","Gökçukur",536,"mutlu@gmail.com","123mutlu"))
            Database.execSQL("INSERT INTO personeller(Otopark_ID,Personel_Adı,Personel_Soyadı,Personel_Telefon,Personel_EPosta,Personel_Sifre) VALUES(?,?,?,?,?,?)",
                arrayOf(2,"İsmail","Aydın",544,"ismail@gmail.com","123ismail"))
        }
        if(!Tablolar.contains("ucretlendirme"))
        {
            Database.execSQL("CREATE TABLE ucretlendirme(Ucret_ID INTEGER PRIMARY KEY AUTOINCREMENT,Arac_Sınıf INT,Saatlik_Ucret INT,Gunluk_Ucret INT,Aylık_Ucret INT,FOREIGN KEY(Arac_Sınıf) REFERENCES araclar(Arac_Sınıf))")
            Database.execSQL("INSERT INTO ucretlendirme(Arac_Sınıf,Saatlik_Ucret,Gunluk_Ucret,Aylık_Ucret) VALUES(?,?,?,?)",
                arrayOf(1,20,50,1000))
            Database.execSQL("INSERT INTO ucretlendirme(Arac_Sınıf,Saatlik_Ucret,Gunluk_Ucret,Aylık_Ucret) VALUES(?,?,?,?)",
                arrayOf(2,50,100,2500))
        }
        if(!Tablolar.contains("park_halindeki_araclar"))
        {
            Database.execSQL("CREATE TABLE park_halindeki_araclar(Arac_Plaka VARCHAR PRIMARY KEY,Otopark_ID INT,Giris_Tarih DATE,Giris_Saat TIME,Arac_Blok VARCHAR,Blok_Numara INT,FOREIGN KEY(Otopark_ID) REFERENCES otoparklar(Otopark_ID),FOREIGN KEY(Arac_Plaka) REFERENCES araclar(Arac_Plaka))")
        }
        if(!Tablolar.contains("musteriler"))
        {
            Database.execSQL("CREATE TABLE musteriler(Musteri_ID INTEGER PRIMARY KEY AUTOINCREMENT,Adı VARCHAR,Soyadı VARCHAR,Telefon_Numara INT,EPosta VARCHAR,Sifre VARCHAR)")
        }
        if(!Tablolar.contains("sikayetler"))
        {
            Database.execSQL("CREATE TABLE IF NOT EXISTS sikayetler(Sikayet_ID INTEGER PRIMARY KEY AUTOINCREMENT,Musteri_ID INT,Sikayet_Edilen_Personel_ID INT,Sikayet_Nedeni_ID INT,Sikayet VARCHAR,FOREIGN KEY(Musteri_ID) REFERENCES musteriler(Musteri_ID),FOREIGN KEY(Sikayet_Edilen_Personel_ID) REFERENCES personeller(Personel_ID),FOREIGN KEY(Sikayet_Nedeni_ID) REFERENCES sikayet_nedenleri(Sikayet_Nedeni_ID))")
        }
        if(!Tablolar.contains("sikayet_nedenleri"))
        {
            Database.execSQL("CREATE TABLE sikayet_nedenleri(Sikayet_Nedeni_ID INTEGER PRIMARY KEY,Sikayet_Nedeni VARCHAR)")
        }
        if(!Tablolar.contains("araclar"))
        {
            Database.execSQL("CREATE TABLE IF NOT EXISTS araclar(Musteri_ID INTEGER,Arac_Plaka VARCHAR PRIMARY KEY,Arac_ID INTEGER,Arac_Sınıf INT,FOREIGN KEY(Musteri_ID) REFERENCES musteriler(Musteri_ID),FOREIGN KEY(Arac_ID) REFERENCES markalar(Arac_ID))")
        }
    }

    fun UyeOl(view:View)
    {

        intent= Intent(applicationContext,UyeOlma::class.java)
        startActivity(intent)
    }

    fun UyeGiris(view:View)
    {
        intent= Intent(applicationContext,UyeGiris::class.java)
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.girismenu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.cikis)
        {
            finish()
        }
        else if(item.itemId==R.id.ucretler)
        {
            val intent=Intent(applicationContext,MusteriUcretler::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}