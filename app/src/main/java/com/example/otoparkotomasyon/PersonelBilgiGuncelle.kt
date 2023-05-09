package com.example.otoparkotomasyon

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_personel_bilgi_guncelle.*
import kotlinx.android.synthetic.main.activity_uye_olma.*

class PersonelBilgiGuncelle : AppCompatActivity() {
    private var ID:Int=0
    private lateinit var secilipersonel:String
    private lateinit var isim:String
    private lateinit var soyisim:String
    private lateinit var Personeller:MutableList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personel_bilgi_guncelle)
        ID=intent.getIntExtra("ID",0)
        Personeller= mutableListOf<String>()
        try {
            Personeller()
            SpnPersonelBilgiGuncellePersonel.onItemSelectedListener=object : AdapterView.OnItemSelectedListener
            {
                override fun onItemSelected (p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    secilipersonel=Personeller.get(p2)    //Hangi marka seçildi
                    isim=secilipersonel.split(" ")[0]
                    soyisim=secilipersonel.split(" ")[1]
                    PersonelVeriler(isim,soyisim)
                }
                override fun onNothingSelected(p0: AdapterView<*>?)
                {
                    Toast.makeText(applicationContext,"Lütfen bir marka seçiniz", Toast.LENGTH_LONG).show()
                }
            }
        }
        catch (e:java.lang.Exception)
        {
        }
    }

    fun PersonelSil(view:View)
    {
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val alert=AlertDialog.Builder(this)
        val personel=Database.rawQuery("SELECT * FROM personeller WHERE Personel_Adı=? AND Personel_Soyadı=?",
            arrayOf(isim,soyisim))
        if(personel.count>0)
        {
            alert.setTitle("Emin misiniz?")
            alert.setMessage("Seçili personel siliniyor\nEmin misiniz?\nSeçili personel: ${secilipersonel}")
            alert.setPositiveButton("Evet"){dialog,which->
                Database.execSQL("DELETE FROM personeller WHERE Personel_Adı=? AND Personel_Soyadı=?",
                    arrayOf(isim,soyisim))
                Toast.makeText(applicationContext,"Silme işlemi başarıyla gerçekleştirildi",Toast.LENGTH_LONG).show()
                Personeller.remove(isim+" "+soyisim)
            }
            alert.setNegativeButton("Hayır"){dialog,which->}
            alert.show()
        }
    }

    @SuppressLint("NotConstructor")
    fun PersonelBilgiGuncelle(view:View)
    {
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val alert=AlertDialog.Builder(this)
        alert.setTitle("Emin misiniz?")
        alert.setMessage("Bilgileriniz güncelleniyor\nGüncellemek istediğinizden emin misiniz?")
        alert.setPositiveButton("Evet"){dialog,which->
            if(txtPersonelBilgiGuncelleSoyisim.text.toString()!=""&&txtPersonelBilgiGuncelleSoyisim.text.toString()!=""&&txtPersonelBilgiGuncelleTelefon.text.toString()!=""&&
                txtPersonelBilgiGuncelleEPosta.text.toString()!=""&&txtPersonelBilgiGuncelleSifre.text.toString()!="")
            {
                try
                {
                    Database.execSQL("UPDATE personeller SET Personel_Adı=?,Personel_Soyadı=?,Personel_Telefon=?,Personel_EPosta=?,Personel_Sifre=? WHERE Personel_Adı=? AND Personel_Soyadı=?",
                        arrayOf(txtPersonelBilgiGuncelleİsim.text.toString(),
                            txtPersonelBilgiGuncelleSoyisim.text.toString(),
                            txtPersonelBilgiGuncelleTelefon.text.toString(),
                            txtPersonelBilgiGuncelleEPosta.text.toString(),
                            txtPersonelBilgiGuncelleSifre.text.toString(),isim,soyisim))
                    Toast.makeText(applicationContext,"Bilgileriniz başarıyla güncellendi\nGüvenlik açısından hesaptan çıkartılıyorsunuz",Toast.LENGTH_LONG).show()
                    val intent=Intent(applicationContext,UyeGiris::class.java)
                    startActivity(intent)
                    finish()
                }
                catch (e:java.lang.Exception)
                {
                    Toast.makeText(applicationContext,"Bir hata meydana geldi\nTekrar deneyiniz",Toast.LENGTH_LONG).show()
                }
            }
            else
            {
                Toast.makeText(applicationContext,"Tüm bilgileri doldurduğunuzdan emin olun!!!",Toast.LENGTH_LONG).show()
            }
            alert.setNegativeButton("Hayır"){dialog,which->}
        }
        alert.show()
    }
    fun Personeller()
    {
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val personelcursor=Database.rawQuery("SELECT Personel_Adı,Personel_Soyadı FROM personeller",null)
        if(personelcursor.count>0)
        {
            while (personelcursor.moveToNext())
            {
                val personel=personelcursor.getString(personelcursor.getColumnIndex("Personel_Adı").toInt())+" "+personelcursor.getString(personelcursor.getColumnIndex("Personel_Soyadı").toInt())
                Personeller.add(personel)
            }
        }
        val adapter= ArrayAdapter(this,R.layout.spinnerselectitem,Personeller)
        adapter.setDropDownViewResource(R.layout.spinnerdropitem)
        SpnPersonelBilgiGuncellePersonel.adapter=adapter
    }
    fun PersonelVeriler(personeladi:String,personelsoyadi:String)
    {
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val personelcursor=Database.rawQuery("SELECT * FROM personeller WHERE Personel_Adı=? AND Personel_Soyadı=?", arrayOf(personeladi,personelsoyadi))
        if(personelcursor.count>0)
        {
            while(personelcursor.moveToNext())
            {
                txtPersonelBilgiGuncelleİsim.setText(personelcursor.getString(personelcursor.getColumnIndex("Personel_Adı").toInt()).toString())
                txtPersonelBilgiGuncelleSoyisim.setText(personelcursor.getString(personelcursor.getColumnIndex("Personel_Soyadı").toInt()).toString())
                txtPersonelBilgiGuncelleTelefon.setText(personelcursor.getInt(personelcursor.getColumnIndex("Personel_Telefon").toInt()).toString())
                txtPersonelBilgiGuncelleEPosta.setText(personelcursor.getString(personelcursor.getColumnIndex("Personel_EPosta").toInt()).toString())
                txtPersonelBilgiGuncelleSifre.setText(personelcursor.getString(personelcursor.getColumnIndex("Personel_Sifre").toInt()).toString())
            }
        }
    }
}