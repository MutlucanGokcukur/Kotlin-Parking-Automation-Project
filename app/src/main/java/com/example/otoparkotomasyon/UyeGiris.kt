package com.example.otoparkotomasyon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_uye_giris.*

class UyeGiris : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uye_giris)
        val database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        title="Giriş"
    }

    fun Giris(view:View)
    {
        val alert=AlertDialog.Builder(this)
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        if(UyeGirisEPosta.text.toString()!="" &&UyeGirisSifre.text.toString()!="")
        {
                val mustericursor=Database.rawQuery("SELECT * FROM musteriler WHERE EPosta=? AND Sifre=?",
                    arrayOf(UyeGirisEPosta.text.toString(),UyeGirisSifre.text.toString()))
                if(mustericursor.count>0) {
                    mustericursor.moveToFirst()
                    val ID=mustericursor.getInt(mustericursor.getColumnIndex("Musteri_ID").toInt()).toInt()
                    val intent = Intent(applicationContext, UyeAnaSayfa::class.java)
                    intent.putExtra("ID",ID)
                    startActivity(intent)
                    finish()
                }
                else
                {

                    val personelcursor=Database.rawQuery("SELECT * FROM personeller WHERE Personel_EPosta=? AND Personel_Sifre=?",
                        arrayOf(UyeGirisEPosta.text.toString(),UyeGirisSifre.text.toString()))
                    if(personelcursor.count>0)
                    {
                        personelcursor.moveToFirst()
                        val ID=personelcursor.getInt(personelcursor.getColumnIndex("Personel_ID").toInt())
                        if(ID==1)
                        {
                            val intent = Intent(applicationContext, PersonelSayfa::class.java)
                            intent.putExtra("ID",ID)
                            startActivity(intent)
                            finish()
                        }
                        else
                        {
                            val intent = Intent(applicationContext, CalisanSayfa::class.java)
                            intent.putExtra("ID",ID)
                            startActivity(intent)
                            finish()
                        }
                    }
                    else
                    {
                        alert.setTitle("Hata")
                        alert.setMessage("Böyle bir hesap bulunamadı\nBilgilerinizi kontrol ediniz.")
                        alert.setNegativeButton("Tamam"){dialog,which->
                        }
                        alert.show()
                    }
                }
        }
        else
        {
            Toast.makeText(applicationContext,"Lütfen bilgilerinizi doldurunuz",Toast.LENGTH_LONG).show()
        }
    }
}