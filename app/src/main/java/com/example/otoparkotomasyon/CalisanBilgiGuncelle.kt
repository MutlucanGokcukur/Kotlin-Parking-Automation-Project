package com.example.otoparkotomasyon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_calisan_bilgi_guncelle.*

class CalisanBilgiGuncelle : AppCompatActivity() {
    private var ID:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calisan_bilgi_guncelle)
        ID=intent.getIntExtra("ID",0)
        CalisanVeriler()
    }

    fun CalisanVeriler()
    {
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val calisanbilgi=Database.rawQuery("SELECT * FROM personeller WHERE Personel_ID=?", arrayOf(ID.toString()))
        if(calisanbilgi.count>0)
        {
            while (calisanbilgi.moveToNext())
            {
                txtCalisanBilgiGuncelleİsim.setText(calisanbilgi.getString(calisanbilgi.getColumnIndex("Personel_Adı").toInt()))
                txtCalisanBilgiGuncelleSoyisim.setText(calisanbilgi.getString(calisanbilgi.getColumnIndex("Personel_Soyadı").toInt()))
                txtCalisanBilgiGuncelleTelefon.setText(calisanbilgi.getInt(calisanbilgi.getColumnIndex("Personel_Telefon").toInt()).toString())
                txtCalisanBilgiGuncelleEPosta.setText(calisanbilgi.getString(calisanbilgi.getColumnIndex("Personel_EPosta").toInt()))
                txtCalisanBilgiGuncelleSifre.setText(calisanbilgi.getString(calisanbilgi.getColumnIndex("Personel_Sifre").toInt()))
            }
        }
    }
    fun BilgiGuncelle(view:View)
    {
        try
        {
            val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
            val alert=AlertDialog.Builder(this)
            alert.setTitle("Emin misiniz?")
            alert.setMessage("Bilgileriniz güncelleniyor.\nBilgilerinizin doğrulundan emin misiniz?")
            alert.setPositiveButton("Evet"){dialog,which->
                Database.execSQL("UPDATE personeller SET Personel_Adı=?,Personel_Soyadı=?,Personel_Telefon=?,Personel_EPosta=?,Personel_Sifre=? WHERE Personel_ID=?",
                    arrayOf(txtCalisanBilgiGuncelleİsim.text.toString(),
                        txtCalisanBilgiGuncelleSoyisim.text.toString(),
                        txtCalisanBilgiGuncelleTelefon.text.toString(),
                        txtCalisanBilgiGuncelleEPosta.text.toString(),
                        txtCalisanBilgiGuncelleSifre.text.toString(),ID))
                Toast.makeText(applicationContext,"Güvenlik sebebi ile hesaptan çıkartılıyorsunuz\nTekrar giriş yapınız",Toast.LENGTH_LONG).show()
                val intent=Intent(applicationContext,UyeGiris::class.java)
                startActivity(intent)
                finish()
            }
            alert.setNegativeButton("Hayır"){dialog,which->}
            alert.show()
        }
        catch (e:java.lang.Exception)
        {
            Toast.makeText(applicationContext,"Hata meydana geldi",Toast.LENGTH_LONG).show()
        }

    }
}