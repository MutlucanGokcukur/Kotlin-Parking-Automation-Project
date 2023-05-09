package com.example.otoparkotomasyon

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_uye_ana_sayfa.*
import kotlinx.android.synthetic.main.activity_uye_olma.*

class UyeAnaSayfa : AppCompatActivity() {
    private var ID:Int=0
    private var OtoparkID:Int=0
    private lateinit var AracBlok:String
    private lateinit var Personeller:MutableList<String>
    private lateinit var PersonellerTelefon:MutableList<String>
    private var BlokNumara:Int=0
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uye_ana_sayfa)
        ID=intent.getIntExtra("ID",0)
        Personeller= mutableListOf<String>()
        PersonellerTelefon= mutableListOf<String>()
        btnUyeAnaSayfaCikis.isVisible=false
        txtUyeAnaSayfaGirisTarih.setText("Giris Tarih: Veri Bulunamaadı")
        txtUyeAnaSayfaGirisSaat.setText("Giris Saat: Veri Bulunamadı")
        txtUyeAnaSayfaPlaka.setText("Plaka: Veri Bulunamadı")
        txtUyeAnaSayfaPersonelTelefon.setText("Personel Telefon: Veri Bulunamadı")
        txtUyeAnaSayfaAracKonum.setText("Araç Konum: Veri Bulunamadı")
        SpnUyeAnaSayfaPersonel.isVisible=false
        VeriKontrol()
        try {
            val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
            val arabasayıcursor=Database.rawQuery("SELECT Otopark_ID from park_halindeki_araclar WHERE Otopark_ID=?",arrayOf(OtoparkID.toString()))
            val kapasitecursor=Database.rawQuery("SELECT Otopark_Adı,Kapasite from otoparklar WHERE Otopark_ID=?", arrayOf(OtoparkID.toString()))
            var kapasite=0
            var otoparkadi=""
            if(kapasitecursor.count>0)
            {
                while (kapasitecursor.moveToNext())
                {
                    kapasite=kapasitecursor.getInt(kapasitecursor.getColumnIndex("Kapasite").toInt())
                    otoparkadi=kapasitecursor.getString(kapasitecursor.getColumnIndex("Otopark_Adı").toInt())
                }
            }
            var toplamarac=0
            if(arabasayıcursor.count>0)
            {
                while(arabasayıcursor.moveToNext())
                {
                    toplamarac=toplamarac+1
                }
            }
            txtUyeAnaSayfaOtopark.text="${otoparkadi}: ${toplamarac.toString()}/${kapasite.toString()}"
            //https://www.youtube.com/watch?v=xU-Cc41DfTg
            UyeAnaSayfaPrgBarKocaeliOtopark.max=kapasite
            ObjectAnimator.ofInt(UyeAnaSayfaPrgBarKocaeliOtopark,"Progress",toplamarac).setDuration(2000).start()
            SpnUyeAnaSayfaPersonel.onItemSelectedListener=object : AdapterView.OnItemSelectedListener
            {
                override fun onItemSelected (p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                    val telefon=PersonellerTelefon.get(p2)    //Hangi personel seçildi ve onun telefonu
                    txtUyeAnaSayfaPersonelTelefon.setText("Personel Telefon: ${telefon}")
                }
                override fun onNothingSelected(p0: AdapterView<*>?)
                {
                    Toast.makeText(applicationContext,"Lütfen bir model seçiniz", Toast.LENGTH_LONG).show()
                }
            }
        }
        catch (e:java.lang.Exception)
        {

        }

    }
    @SuppressLint("SetTextI18n")
    fun VeriKontrol()
    {
        try
        {
            val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
            val musteriaraba=Database.rawQuery("SELECT * FROM park_halindeki_araclar INNER JOIN araclar ON araclar.Arac_Plaka=park_halindeki_araclar.Arac_Plaka INNER JOIN personeller ON personeller.Otopark_ID=park_halindeki_araclar.Otopark_ID WHERE araclar.Musteri_ID=?", arrayOf(ID.toString()))
            if(musteriaraba.count>0)
            {
                txtUyeAnaSayfaGirisSaat.isVisible=true
                txtUyeAnaSayfaGirisTarih.isVisible=true
                SpnUyeAnaSayfaPersonel.isVisible=true
                txtUyeAnaSayfaPersonelTelefon.isVisible=true
                txtUyeAnaSayfaPlaka.isVisible=true
                textView5.isVisible=true
                while(musteriaraba.moveToNext())
                {
                    OtoparkID=musteriaraba.getInt(musteriaraba.getColumnIndex("Otopark_ID").toInt())
                    txtUyeAnaSayfaPlaka.setText("Plaka: "+musteriaraba.getString(musteriaraba.getColumnIndex("Arac_Plaka").toInt()))
                    PersonellerTelefon.add(musteriaraba.getInt(musteriaraba.getColumnIndex("Personel_Telefon").toInt()).toString())
                    txtUyeAnaSayfaGirisSaat.setText("Giriş Saati: "+musteriaraba.getString(musteriaraba.getColumnIndex("Giris_Saat").toInt()).toString())
                    Personeller.add(musteriaraba.getString(musteriaraba.getColumnIndex("Personel_Adı").toInt())+" "+musteriaraba.getString(musteriaraba.getColumnIndex("Personel_Soyadı").toInt()).toString())
                    txtUyeAnaSayfaGirisTarih.setText("Giriş Tarih: "+musteriaraba.getString(musteriaraba.getColumnIndex("Giris_Tarih").toInt()).toString())
                    AracBlok=musteriaraba.getString(musteriaraba.getColumnIndex("Arac_Blok").toInt())
                    BlokNumara=musteriaraba.getInt(musteriaraba.getColumnIndex("Blok_Numara").toInt())
                    txtUyeAnaSayfaAracKonum.setText("Araç Konum: ${AracBlok} / ${BlokNumara}")
                    btnUyeAnaSayfaCikis.isVisible=true
                    btnUyeSayfaQRKod.isVisible=false
                }
                //https://www.youtube.com/watch?v=N8GfosWTt44
                val adapter= ArrayAdapter(this,R.layout.spinnerselectitem,Personeller)
                adapter.setDropDownViewResource(R.layout.spinnerdropitem)
                SpnUyeAnaSayfaPersonel.adapter=adapter
            }
        }
        catch (e:java.lang.Exception)
        {

        }
    }

    fun QROkuma(view: View)
    {
        val intent= Intent(applicationContext,QRGiris::class.java)
        intent.putExtra("ID",ID)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.uyeanasayfamenu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.hesapsil)
        {
            val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
            val alert=AlertDialog.Builder(this)
            alert.setTitle("Emin misiniz?")
            alert.setMessage("Sizinle alakalı bütün verileriniz silinecektir.\nSilmek istediğinize emin misiniz?")
            alert.setPositiveButton("Evet"){dialog,which->
                try {
                    Database.execSQL("DELETE FROM musteriler WHERE Musteri_ID=?", arrayOf(ID))
                    Database.execSQL("DELETE FROM araclar WHERE Musteri_ID=?", arrayOf(ID))
                    val intent=Intent(applicationContext,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                catch (e:java.lang.Exception)
                {
                    Toast.makeText(applicationContext,"Silinme hatası",Toast.LENGTH_LONG).show()
                }
            }
            alert.setNegativeButton("Hayır"){dialog,which->
            }

            alert.show()
        }
        else if(item.itemId==R.id.guncelle)
        {
            val intent= Intent(applicationContext,UyeBilgiGuncelleme::class.java)
            intent.putExtra("ID",ID)
            startActivity(intent)
        }
        else if(item.itemId==R.id.sikayetolustur)
        {
            val intent=Intent(applicationContext,UyeSikayetSayfasi::class.java)
            intent.putExtra("ID",ID)
            startActivity(intent)
        }
        else if(item.itemId==R.id.cikis)
        {
            val intent=Intent(applicationContext,UyeGiris::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun btnUyeSayfaAracCikis(view:View)
    {
        val intent=Intent(applicationContext,QRCikis::class.java)
        intent.putExtra("ID",ID)
        startActivity(intent)
        finish()
    }
}