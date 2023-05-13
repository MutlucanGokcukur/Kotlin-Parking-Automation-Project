package com.example.otoparkotomasyon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_alinacak_odemeler.*
import kotlinx.android.synthetic.main.activity_personel_sayfa.*

class AlinacakOdemeler : AppCompatActivity() {
    private lateinit var İsim:String
    private lateinit var Soyisim:String
    private var ToplamTutar:Int=0
    private lateinit var Plaka:String
    private lateinit var Plakalar:MutableList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alinacak_odemeler)
        title="Alınacak Ödemeler"
        Plakalar= mutableListOf<String>()
       Odenecekler()
        val adapter= ArrayAdapter(this,R.layout.spinnerselectitem,Plakalar)
        LstAlinacakOdemeler.adapter=adapter
        LstAlinacakOdemeler.onItemClickListener=
            AdapterView.OnItemClickListener{ parent, view, position, id->
                Sorgu(Plakalar[position])
        }
    }
    fun Sorgu(plaka:String)
    {
        val alert=AlertDialog.Builder(this)
        alert.setTitle("Emin misiniz?")
        alert.setMessage("Aracın ödemesi alınmıştır. Çıkış yapılıyor\nMüşteri: ${İsim} ${Soyisim}\nAraç Plaka: ${Plaka}\nToplam Tutar: ${ToplamTutar}")
        alert.setPositiveButton("Evet"){dialog,which->
            val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
            Database.execSQL("DELETE FROM odenecekler WHERE Arac_Plaka=?", arrayOf(plaka))
            Toast.makeText(applicationContext,"Çıkış işlemi tamamlandı",Toast.LENGTH_LONG).show()
            val intent= Intent(applicationContext,PersonelSayfa::class.java)
            startActivity(intent)
            finish()
        }
        alert.setNegativeButton("Hayır"){dialog,which->}
        alert.show()
    }

    fun Odenecekler()
    {
        Plakalar.clear()
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        try {
            val odenecekcursor=Database.rawQuery("SELECT * FROM odenecekler INNER JOIN musteriler ON musteriler.Musteri_ID=odenecekler.Musteri_ID",null)
            if(odenecekcursor.count>0)
            {
                while(odenecekcursor.moveToNext())
                {
                    İsim=odenecekcursor.getString(odenecekcursor.getColumnIndex("Adı").toInt())
                    Soyisim=odenecekcursor.getString(odenecekcursor.getColumnIndex("Soyadı").toInt())
                    ToplamTutar=odenecekcursor.getInt(odenecekcursor.getColumnIndex("Toplam_Tutar").toInt())
                    Plaka=odenecekcursor.getString(odenecekcursor.getColumnIndex("Arac_Plaka").toInt())
                    Plakalar.add(Plaka)
                }
            }
        }
        catch (e:java.lang.Exception)
        {
            Toast.makeText(applicationContext,"HATA",Toast.LENGTH_LONG).show()
        }
    }
}