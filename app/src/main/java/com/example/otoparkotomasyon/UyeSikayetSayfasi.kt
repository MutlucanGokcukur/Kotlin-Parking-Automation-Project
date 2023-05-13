package com.example.otoparkotomasyon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_uye_olma.*
import kotlinx.android.synthetic.main.activity_uye_sikayet_sayfasi.*

class UyeSikayetSayfasi : AppCompatActivity() {
    private var ID:Int=0
    private lateinit var Otoparklar:MutableList<String>
    private lateinit var Personeller:MutableList<String>
    private lateinit var secilipersonel:String
    private lateinit var Nedenler:MutableList<String>
    private lateinit var seciliotopark:String
    private lateinit var secilineden:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uye_sikayet_sayfasi)
        title="Şikayet Sayfası"
        ID=intent.getIntExtra("ID",0)
        Otoparklar= mutableListOf<String>()
        Nedenler= mutableListOf<String>()
        Personeller= mutableListOf<String>()
        Otoparklar()
        Nedenler()
        SpnSikayetSayfaOtopark.onItemSelectedListener=object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected (p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                seciliotopark=Otoparklar.get(p2)    //Hangi otopark seçildi
                Personeller(seciliotopark)
            }
            override fun onNothingSelected(p0: AdapterView<*>?)
            {
                Toast.makeText(applicationContext,"Lütfen bir otopark seçiniz", Toast.LENGTH_LONG).show()
            }
        }

        SpnSikayetSayfaPersoneller.onItemSelectedListener=object: AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected (p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                secilipersonel=Personeller.get(p2)    //Hangi personel seçildi
            }
            override fun onNothingSelected(p0: AdapterView<*>?)
            {
                Toast.makeText(applicationContext,"Lütfen bir otopark seçiniz", Toast.LENGTH_LONG).show()
            }
        }

        SpnSikayetSayfaNeden.onItemSelectedListener=object :AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected (p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                secilineden=Nedenler.get(p2)    //Hangi sebep seçildi
            }
            override fun onNothingSelected(p0: AdapterView<*>?)
            {
                Toast.makeText(applicationContext,"Lütfen bir otopark seçiniz", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun Otoparklar()
    {
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val otoparkcursor=Database.rawQuery("SELECT Otopark_Adı from otoparklar",null)
        while(otoparkcursor.moveToNext())
        {
            Otoparklar.add(otoparkcursor.getString(otoparkcursor.getColumnIndex("Otopark_Adı").toInt()).toString())
        }
        val adapter= ArrayAdapter(this,R.layout.spinnerselectitem,Otoparklar)
        adapter.setDropDownViewResource(R.layout.spinnerdropitem)
        SpnSikayetSayfaOtopark.adapter=adapter
    }

    fun Nedenler()
    {
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val nedenlercursor=Database.rawQuery("SELECT Sikayet_Nedeni FROM sikayet_nedenleri",null)
        while(nedenlercursor.moveToNext())
        {
            Nedenler.add(nedenlercursor.getString(nedenlercursor.getColumnIndex("Sikayet_Nedeni").toInt()).toString())
        }
        val adapter= ArrayAdapter(this,R.layout.spinnerselectitem,Nedenler)
        adapter.setDropDownViewResource(R.layout.spinnerdropitem)
        SpnSikayetSayfaNeden.adapter=adapter
    }
    fun Personeller(gelenotopark:String)
    {
        Personeller.clear()
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val personelcursor=Database.rawQuery("SELECT Personel_Adı from personeller INNER JOIN otoparklar ON otoparklar.Otopark_ID=personeller.Otopark_ID WHERE Otopark_Adı=?",
            arrayOf(gelenotopark))
        while (personelcursor.moveToNext())
        {
            Personeller.add(personelcursor.getString(personelcursor.getColumnIndex("Personel_Adı").toInt()).toString())
        }
        val adapter= ArrayAdapter(this,R.layout.spinnerselectitem,Personeller)
        adapter.setDropDownViewResource(R.layout.spinnerdropitem)
        SpnSikayetSayfaPersoneller.adapter=adapter
    }

    fun SikayetOlustur(view:View)
    {
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val personelidcursor=Database.rawQuery("SELECT personeller.Otopark_ID ,Personel_ID from personeller INNER JOIN otoparklar On otoparklar.Otopark_ID=personeller.Otopark_ID WHERE Otopark_Adı=?", arrayOf(seciliotopark))
        var personelid=0
        var otoparkid=0
        while (personelidcursor.moveToNext())
        {
            personelid=personelidcursor.getInt(personelidcursor.getColumnIndex("Personel_ID").toInt())
            otoparkid=personelidcursor.getInt(personelidcursor.getColumnIndex("Otopark_ID").toInt())
        }

        val sikayetnedenid=Database.rawQuery("SELECT Sikayet_Nedeni_ID from sikayet_nedenleri WHERE Sikayet_Nedeni=?", arrayOf(secilineden))
        var sikayetid=0
        while(sikayetnedenid.moveToNext())
        {
            sikayetid=sikayetnedenid.getInt(sikayetnedenid.getColumnIndex("Sikayet_Nedeni_ID").toInt())
        }


        val alert=AlertDialog.Builder(this)
        alert.setTitle("Emin misiniz?")
        alert.setMessage("Şikayetiniz oluşturulup ilgili personellere iletilecek.\nŞikayetinizden emin misiniz?")
        alert.setPositiveButton("Evet"){dialog,which->
            try {
                Database.execSQL("INSERT INTO sikayetler(Otopark_ID,Musteri_ID,Sikayet_Edilen_Personel_ID,Sikayet_Nedeni_ID,Sikayet) VALUES(?,?,?,?,?)",
                    arrayOf(otoparkid,ID,personelid,sikayetid,txtSikayetSayfaSikayet.text.toString()))
                Toast.makeText(applicationContext,"Şikayetiniz başarıyla alındı", Toast.LENGTH_LONG).show()
            }
            catch (e:java.lang.Exception)
            {
                Toast.makeText(applicationContext,"HATA", Toast.LENGTH_LONG).show()
            }
        }
        alert.setNegativeButton("Hayır"){dialog,which->}
         alert.show()
    }
}