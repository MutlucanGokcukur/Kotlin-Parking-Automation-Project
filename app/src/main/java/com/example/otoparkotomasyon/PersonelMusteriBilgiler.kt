package com.example.otoparkotomasyon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_alinacak_odemeler.*
import kotlinx.android.synthetic.main.activity_personel_musteri_bilgiler.*

class PersonelMusteriBilgiler : AppCompatActivity() {
    private lateinit var Musteriler:MutableList<String>
    private lateinit var musterisim:String
    private lateinit var musterisoyisim:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personel_musteri_bilgiler)
        Musteriler= mutableListOf<String>()
        title="Müşteri Bilgiler"
        Musteriler()
        val adapter= ArrayAdapter(this,R.layout.spinnerselectitem,Musteriler)
        LstPersonelMusteriBilgiler.adapter=adapter
        LstPersonelMusteriBilgiler.onItemClickListener=
            AdapterView.OnItemClickListener{ parent, view, position, id->
                musterisim=Musteriler[position].split(" ")[0]
                musterisoyisim=Musteriler[position].split(" ")[1]
                SeciliMusteri(musterisim,musterisoyisim)
            }
    }
    fun Musteriler()
    {
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val musteriler=Database.rawQuery("SELECT * FROM musteriler",null)
        if(musteriler.count>0)
        {
            while (musteriler.moveToNext())
            {
                Musteriler.add(musteriler.getString(musteriler.getColumnIndex("Adı").toInt())+" "+musteriler.getString(musteriler.getColumnIndex("Soyadı").toInt()))
            }
        }
    }

    fun SeciliMusteri(isim:String,soyisim:String)
    {
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val musteriler=Database.rawQuery("SELECT * FROM musteriler INNER JOIN araclar on araclar.Musteri_ID=musteriler.Musteri_ID WHERE Adı=? AND Soyadı=?",arrayOf(isim,soyisim))
        if(musteriler.count>0)
        {
            val alert=AlertDialog.Builder(this)
            var Telefon=0
            var EPosta=""
            var plaka=""
            while (musteriler.moveToNext())
            {
                Telefon=musteriler.getInt(musteriler.getColumnIndex("Telefon_Numara").toInt())
                EPosta=musteriler.getString(musteriler.getColumnIndex("EPosta").toInt())
                plaka=musteriler.getString(musteriler.getColumnIndex("Arac_Plaka").toInt())
            }
            alert.setTitle("Bilgiler")
            alert.setMessage("Ad Soyad: ${isim} ${soyisim}\nTelefon: ${Telefon}\nE-Mail Adres: ${EPosta}\nPlaka: ${plaka}")
            alert.setPositiveButton("Tamam"){dialog,which->}
            alert.show()
        }
    }
}