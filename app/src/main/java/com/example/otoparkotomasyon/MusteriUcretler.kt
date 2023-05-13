package com.example.otoparkotomasyon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_musteri_ucretler.*
import kotlinx.android.synthetic.main.activity_ucret_guncelleme.*

class MusteriUcretler : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_musteri_ucretler)
        txtMusteriUcretSaatlik.isEnabled=false
        txtMusteriUcretGunluk.isEnabled=false
        txtMusteriUcretAylık.isEnabled=false
        title="Ücretlendirmeler"
    }

    fun BirUcret(view: View)
    {
        if(chcMusteriUcretBirSınıf.isChecked)
        {
            chcMusteriUcretİkiSınıf.isEnabled=false
            try {
                val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
                val birincisinif=Database.rawQuery("SELECT Saatlik_Ucret,Gunluk_Ucret,Aylık_Ucret from ucretlendirme WHERE Arac_Sınıf=1",null)
                if(birincisinif.count>0)
                {
                    while (birincisinif.moveToNext())
                    {
                        txtMusteriUcretSaatlik.setText(birincisinif.getInt(birincisinif.getColumnIndex("Saatlik_Ucret").toInt()).toString())
                        txtMusteriUcretGunluk.setText(birincisinif.getInt(birincisinif.getColumnIndex("Gunluk_Ucret").toInt()).toString())
                        txtMusteriUcretAylık.setText(birincisinif.getInt(birincisinif.getColumnIndex("Aylık_Ucret").toInt()).toString())
                    }
                }
            }
            catch (e:java.lang.Exception)
            {
                Toast.makeText(applicationContext,"Bir hata meydana geldi",Toast.LENGTH_LONG).show()
            }

        }
        else if(!chcMusteriUcretBirSınıf.isChecked)
        {
            chcMusteriUcretİkiSınıf.isEnabled=true
        }
    }

    fun İkiUcret(view:View)
    {
        if(chcMusteriUcretİkiSınıf.isChecked)
        {
            chcMusteriUcretBirSınıf.isEnabled=false
            try {
                val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
                val birincisinif=Database.rawQuery("SELECT Saatlik_Ucret,Gunluk_Ucret,Aylık_Ucret from ucretlendirme WHERE Arac_Sınıf=2",null)
                if(birincisinif.count>0)
                {
                    while (birincisinif.moveToNext())
                    {
                        txtMusteriUcretSaatlik.setText(birincisinif.getInt(birincisinif.getColumnIndex("Saatlik_Ucret").toInt()).toString())
                        txtMusteriUcretGunluk.setText(birincisinif.getInt(birincisinif.getColumnIndex("Gunluk_Ucret").toInt()).toString())
                        txtMusteriUcretAylık.setText(birincisinif.getInt(birincisinif.getColumnIndex("Aylık_Ucret").toInt()).toString())
                    }
                }
            }
            catch (e:java.lang.Exception)
            {
                Toast.makeText(applicationContext,"Bir hata meydana geldi",Toast.LENGTH_LONG).show()
            }
        }
        else if(!chcMusteriUcretİkiSınıf.isChecked)
        {
            chcMusteriUcretBirSınıf.isEnabled=true
        }
    }
}