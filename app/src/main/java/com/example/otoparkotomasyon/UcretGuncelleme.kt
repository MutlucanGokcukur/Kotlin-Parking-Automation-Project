package com.example.otoparkotomasyon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_ucret_guncelleme.*

class UcretGuncelleme : AppCompatActivity() {
    private var aracsinif:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ucret_guncelleme)
        txtUcretGuncellemeAylık.isEnabled=false
        txtUcretGuncellemeGunluk.isEnabled=false
        txtUcretGuncellemeSaatlik.isEnabled=false
        title="Ücret Güncelleme"
    }
    fun BirGoster(view:View)
    {
        chcUcretGuncellemeBirSınıf.isEnabled=true
        if(chcUcretGuncellemeBirSınıf.isChecked)
        {
            aracsinif=1
            chcUcretGuncellemeİkiSınıf.isEnabled=false
            txtUcretGuncellemeAylık.isEnabled=true
            txtUcretGuncellemeGunluk.isEnabled=true
            txtUcretGuncellemeSaatlik.isEnabled=true
            val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
            var saatlik=0
            var gunluk=0
            var aylik=0
            val ucretcursor=Database.rawQuery("SELECT Saatlik_Ucret,Gunluk_Ucret,Aylık_Ucret from ucretlendirme WHERE Arac_Sınıf=1",null)
            if(ucretcursor.count>0)
            {
                while (ucretcursor.moveToNext())
                {
                    saatlik=ucretcursor.getInt(ucretcursor.getColumnIndex("Saatlik_Ucret").toInt())
                    gunluk=ucretcursor.getInt(ucretcursor.getColumnIndex("Gunluk_Ucret").toInt())
                    aylik=ucretcursor.getInt(ucretcursor.getColumnIndex("Aylık_Ucret").toInt())
                }
            }
            txtUcretGuncellemeAylık.setText(aylik.toString())
            txtUcretGuncellemeGunluk.setText(gunluk.toString())
            txtUcretGuncellemeSaatlik.setText(saatlik.toString())
        }
        else if(chcUcretGuncellemeBirSınıf.isChecked==false)
        {
            txtUcretGuncellemeAylık.setText("Sınıf Seçin")
            txtUcretGuncellemeGunluk.setText("Sınıf Seçin")
            txtUcretGuncellemeSaatlik.setText("Sınıf Seçin")
            txtUcretGuncellemeAylık.isEnabled=false
            txtUcretGuncellemeGunluk.isEnabled=false
            txtUcretGuncellemeSaatlik.isEnabled=false
            chcUcretGuncellemeİkiSınıf.isEnabled=true
        }
    }
    fun İkiGoster(view:View)
    {
        chcUcretGuncellemeİkiSınıf.isEnabled=true
        if(chcUcretGuncellemeİkiSınıf.isChecked)
        {
            aracsinif=2
            chcUcretGuncellemeBirSınıf.isEnabled=false
            txtUcretGuncellemeAylık.isEnabled=true
            txtUcretGuncellemeGunluk.isEnabled=true
            txtUcretGuncellemeSaatlik.isEnabled=true
            val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
            var saatlik=0
            var gunluk=0
            var aylik=0
            val ucretcursor=Database.rawQuery("SELECT Saatlik_Ucret,Gunluk_Ucret,Aylık_Ucret from ucretlendirme WHERE Arac_Sınıf=2",null)
            if(ucretcursor.count>0)
            {
                while (ucretcursor.moveToNext())
                {
                    saatlik=ucretcursor.getInt(ucretcursor.getColumnIndex("Saatlik_Ucret").toInt())
                    gunluk=ucretcursor.getInt(ucretcursor.getColumnIndex("Gunluk_Ucret").toInt())
                    aylik=ucretcursor.getInt(ucretcursor.getColumnIndex("Aylık_Ucret").toInt())
                }
            }
            txtUcretGuncellemeAylık.setText(aylik.toString())
            txtUcretGuncellemeGunluk.setText(gunluk.toString())
            txtUcretGuncellemeSaatlik.setText(saatlik.toString())
        }
        else if(chcUcretGuncellemeİkiSınıf.isChecked==false)
        {
            txtUcretGuncellemeAylık.setText("Sınıf Seçin")
            txtUcretGuncellemeGunluk.setText("Sınıf Seçin")
            txtUcretGuncellemeSaatlik.setText("Sınıf Seçin")
            txtUcretGuncellemeAylık.isEnabled=false
            txtUcretGuncellemeGunluk.isEnabled=false
            txtUcretGuncellemeSaatlik.isEnabled=false
            chcUcretGuncellemeBirSınıf.isEnabled=true
        }
    }
    fun UcretGuncelle(view:View)
    {
        if (txtUcretGuncellemeAylık.text.toString()!="Sınıf Seçin")
        {
            if(txtUcretGuncellemeGunluk.text.toString()!="")
            {
                val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
                val alert=AlertDialog.Builder(this)
                alert.setTitle("Emin misiniz?")
                alert.setMessage("Ücretler güncelleniyor.Bundan sonra bu fiyatlar üzerinden işlemler yapılacak.")
                alert.setPositiveButton("Evet"){dialog,which->
                    Database.execSQL("UPDATE ucretlendirme SET Saatlik_Ucret=?,Gunluk_Ucret=?,Aylık_Ucret=? WHERE Arac_Sınıf=?",
                        arrayOf(txtUcretGuncellemeSaatlik.text.toString(),
                        txtUcretGuncellemeGunluk.text.toString(),
                        txtUcretGuncellemeAylık.text.toString(),aracsinif.toString()))
                    Toast.makeText(applicationContext,"Fiyat güncelleme işlemi başarılı\nGüvenlik açısından hesaptan çıkılıyor...",Toast.LENGTH_LONG).show()
                    val intent= Intent(applicationContext,UyeGiris::class.java)
                    startActivity(intent)
                    finish()
                }
                alert.setNegativeButton("Hayır"){dialog,which->}
                alert.show()
            }
        }
        else
        {
            Toast.makeText(applicationContext,"lütfen verileri düzgün giriniz",Toast.LENGTH_LONG).show()
        }

    }
}