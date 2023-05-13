package com.example.otoparkotomasyon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.Intents
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_personel_sayfa.*
import kotlinx.android.synthetic.main.activity_sikayet_listesi.*

class SikayetListesi : AppCompatActivity() {
    private lateinit var Sebepler:MutableList<String>
    private lateinit var SikayetIDListe:MutableList<Int>
    private lateinit var Musteriler:MutableList<String>
    private var ID:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sikayet_listesi)
        ID=intent.getIntExtra("ID",0)
        Musteriler= mutableListOf<String>()
        title="Şikayet Listesi"
        SikayetIDListe= mutableListOf<Int>()
        Sebepler= mutableListOf<String>()
        Sikayetler()
        val adapter= ArrayAdapter(this,R.layout.spinnerselectitem,Musteriler)
        SikayetlerListesi.adapter=adapter
        val alert=AlertDialog.Builder(this)
        SikayetlerListesi.onItemClickListener=
            AdapterView.OnItemClickListener{ parent, view, position, id->
                val kisi=Musteriler[position]
                val sebep=Sebepler[position]
                alert.setTitle("Bilgiler")
                alert.setMessage("Müşteri: ${kisi}\nŞikayet Sebebi: ${sebep}")
                alert.setPositiveButton("Detay"){dialog,which->
                    val intent= Intent(application,SikayetDetaylar::class.java)
                    intent.putExtra("ID",SikayetIDListe[position])
                    startActivity(intent)
                }
                alert.setNegativeButton("Sil"){dialog,which->
                    alert.setTitle("Emin misiniz?")
                    alert.setMessage("Yapılan şikayet listeden siliniyor\nSon kararınız mı?")
                    alert.setPositiveButton("Evet"){dialog,which->
                        try
                        {
                            val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
                            Database.execSQL("DELETE FROM sikayetler WHERE Sikayet_ID=${SikayetIDListe[position]}")
                            Toast.makeText(applicationContext,"Silme İşlemi başarıyla tamamlandı",Toast.LENGTH_LONG).show()
                            Musteriler.remove(kisi)
                            if(ID==1)
                            {
                                val intent= Intent(application,PersonelSayfa::class.java)
                                intent.putExtra("ID",ID)
                                startActivity(intent)
                            }
                            else
                            {
                                val intent= Intent(application,CalisanSayfa::class.java)
                                intent.putExtra("ID",ID)
                                startActivity(intent)
                            }
                        }
                        catch (e:java.lang.Exception)
                        {
                            Toast.makeText(applicationContext,"HATA",Toast.LENGTH_LONG).show()
                        }
                    }
                    alert.setNegativeButton("Hayır"){dialog,which->}
                    alert.show()
                }
                alert.show()
        }
    }

    fun Sikayetler()
    {
        try {
            val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
            val sikayetler=Database.rawQuery("SELECT * FROM sikayetler INNER JOIN musteriler ON musteriler.Musteri_ID=sikayetler.Musteri_ID INNER JOIN sikayet_nedenleri ON sikayet_nedenleri.Sikayet_Nedeni_ID=sikayetler.Sikayet_Nedeni_ID",null)
            if(sikayetler.count>0)
            {
                while(sikayetler.moveToNext())
                {
                    SikayetIDListe.add(sikayetler.getInt(sikayetler.getColumnIndex("Sikayet_ID").toInt()).toInt())
                    var adı=sikayetler.getString(sikayetler.getColumnIndex("Adı").toInt()).toString()
                    var soyadı=sikayetler.getString(sikayetler.getColumnIndex("Soyadı").toInt()).toString()
                    Musteriler.add(adı+" "+soyadı)
                    Sebepler.add(sikayetler.getString(sikayetler.getColumnIndex("Sikayet_Nedeni").toInt()).toString())
                }
            }
        }
        catch (e:java.lang.Exception)
        {

        }

    }
}