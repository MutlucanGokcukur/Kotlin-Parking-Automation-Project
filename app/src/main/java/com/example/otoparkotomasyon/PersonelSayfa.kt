package com.example.otoparkotomasyon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_personel_sayfa.*

class PersonelSayfa : AppCompatActivity() {
    private lateinit var Plakalar:MutableList<String>
    private var ID:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personel_sayfa)
        Plakalar= mutableListOf<String>()
        ID=intent.getIntExtra("ID",0)
        ParkHalindekiAraclar()
        val adapter=ArrayAdapter(this,R.layout.spinnerselectitem,Plakalar)
        LstPersonelSayfa.adapter=adapter
        LstPersonelSayfa.onItemClickListener=AdapterView.OnItemClickListener{ parent, view, position, id->
            PlakaVeriler(Plakalar[position])
        }
    }
    fun PlakaVeriler(plaka:String)
    {
        var tarih=""
        var saat=""
        var blok=""
        var isim=""
        var soyisim=""
        var telefon=0
        var bloknumara=0
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val bilgiler=Database.rawQuery("SELECT * FROM park_halindeki_araclar INNER JOIN araclar ON araclar.Arac_Plaka=park_halindeki_araclar.Arac_Plaka INNER JOIN musteriler ON musteriler.Musteri_ID=araclar.Musteri_ID WHERE park_halindeki_araclar.Arac_Plaka=?", arrayOf(plaka))
        if(bilgiler.count>0)
        {
            while(bilgiler.moveToNext())
            {
                isim=bilgiler.getString(bilgiler.getColumnIndex("Adı").toInt())
                soyisim=bilgiler.getString(bilgiler.getColumnIndex("Soyadı").toInt())
                tarih=bilgiler.getString(bilgiler.getColumnIndex("Giris_Tarih").toInt())
                saat=bilgiler.getString(bilgiler.getColumnIndex("Giris_Saat").toInt())
                blok=bilgiler.getString(bilgiler.getColumnIndex("Arac_Blok").toInt())
                bloknumara=bilgiler.getInt(bilgiler.getColumnIndex("Blok_Numara").toInt())
                telefon=bilgiler.getInt(bilgiler.getColumnIndex("Telefon_Numara").toInt())
            }
            val alert=AlertDialog.Builder(this)
            alert.setTitle("Bilgiler")
            alert.setMessage("Araç Sahibi:${isim} ${soyisim}\nTelefon: ${telefon}\nGiriş: ${tarih} - ${saat}\nAraç Konum: ${blok}/${bloknumara}")
            alert.show()
        }
    }

    fun ParkHalindekiAraclar()
    {
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val otoparkcursor=Database.rawQuery("SELECT * FROM park_halindeki_araclar",null)
        if(otoparkcursor.count>0)
        {
            while(otoparkcursor.moveToNext())
            {
                Plakalar.add(otoparkcursor.getString(otoparkcursor.getColumnIndex("Arac_Plaka").toInt()))
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.personelsayfamenu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.personekle)
        {
            val intent=Intent(applicationContext,PersonelEkle::class.java)
            startActivity(intent)
        }
        else if(item.itemId==R.id.personelguncelle)
        {
            val intent=Intent(applicationContext,PersonelBilgiGuncelle::class.java)
            intent.putExtra("ID",ID)
            startActivity(intent)
        }
        else if(item.itemId==R.id.ucretguncelle)
        {
            val intent=Intent(applicationContext,UcretGuncelleme::class.java)
            startActivity(intent)
        }
        else if(item.itemId==R.id.odemeler)
        {
            val intent=Intent(applicationContext,AlinacakOdemeler::class.java)
            startActivity(intent)
        }
        else if(item.itemId==R.id.musteribilgi)
        {
            val intent=Intent(applicationContext,PersonelMusteriBilgiler::class.java)
            startActivity(intent)
        }
        else if(item.itemId==R.id.cikis)
        {
            val intent=Intent(applicationContext,UyeGiris::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}