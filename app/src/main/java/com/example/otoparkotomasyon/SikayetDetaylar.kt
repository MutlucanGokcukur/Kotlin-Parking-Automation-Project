package com.example.otoparkotomasyon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sikayet_detaylar.*

class SikayetDetaylar : AppCompatActivity() {
    private var ID:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sikayet_detaylar)
        ID=intent.getIntExtra("ID",0)
        title="Şikayet Detaylar"
        Veriler()
    }

    fun Veriler()
    {
        try {
            val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
            val cursor=Database.rawQuery("SELECT * FROM sikayetler INNER JOIN sikayet_nedenleri ON sikayetler.Sikayet_Nedeni_ID=sikayet_nedenleri.Sikayet_Nedeni_ID INNER JOIN musteriler ON musteriler.Musteri_ID=sikayetler.Musteri_ID INNER JOIN otoparklar ON otoparklar.Otopark_ID=sikayetler.Otopark_ID INNER JOIN personeller ON personeller.Personel_ID=sikayetler.Sikayet_Edilen_Personel_ID WHERE Sikayet_ID=?", arrayOf(ID.toString()))
            if(cursor.count>0)
            {
                while(cursor.moveToNext())
                {
                    txtSikayetDetaylarMusteri.setText("Müşteri: "+cursor.getString(cursor.getColumnIndex("Adı").toInt()).toString()+" "+cursor.getString(cursor.getColumnIndex("Soyadı").toInt()).toString())
                    txtSikayetDetaylarPersonel.setText("Şikayet Edilen Personel:\n"+cursor.getString(cursor.getColumnIndex("Personel_Adı").toInt()).toString()+" "+cursor.getString(cursor.getColumnIndex("Personel_Soyadı").toInt()).toString())
                    txtSikayetDetaylarPersonel.setText("Şikayet Edilen Otopark: \n"+cursor.getString(cursor.getColumnIndex("Otopark_Adı").toInt()).toString())
                    txtSikayetDetaylarSikayet.setText("Şikayet:\n"+cursor.getString(cursor.getColumnIndex("Sikayet").toInt()).toString())
                    txtSikayetDetaylarSikayetNedeni.setText("Şikayet Nedeni:\n"+cursor.getString(cursor.getColumnIndex("Sikayet_Nedeni").toInt()).toString())
                }
            }
        }
        catch (e:java.lang.Exception)
        {
            Toast.makeText(applicationContext,"HATA",Toast.LENGTH_LONG).show()
        }
    }
}