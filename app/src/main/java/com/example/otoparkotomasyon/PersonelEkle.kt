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
import kotlinx.android.synthetic.main.activity_personel_ekle.*
import kotlinx.android.synthetic.main.activity_uye_olma.*

class PersonelEkle : AppCompatActivity() {
    private lateinit var Otoparklar:MutableList<String>
    private lateinit var OtoparklarID:MutableList<Int>
    private lateinit var seciliotopark:String
    private var seciliotoparkid:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personel_ekle)
        Otoparklar= mutableListOf<String>()
        OtoparklarID= mutableListOf<Int>()
        Otoparklar()
        SpnPersonelEkleOtopark.onItemSelectedListener=object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected (p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                seciliotopark=Otoparklar.get(p2)
                seciliotoparkid=OtoparklarID.get(p2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?)
            {
                Toast.makeText(applicationContext,"Lütfen bir otopark seçiniz", Toast.LENGTH_LONG).show()
            }
        }
    }
    @SuppressLint("NotConstructor")
    fun PersonelEkle(view:View)
    {
        if(txtPersonelEkleİsim.text.toString()!=""&&txtPersonelEkleSoyisim.text.toString()!=""&&txtPersonelEkleTelefon.text.toString()!=""&&
            txtPersonelEkleEPosta.text.toString()!=""&&txtPersonelEkleSifre.text.toString()!=""&&txtPersonelEkleSifreTekrar.text.toString()!="")
        {
            if(txtPersonelEkleSifre.text.toString()==txtPersonelEkleSifreTekrar.text.toString())
            {
                val alert=AlertDialog.Builder(this)
                try {
                    val eposta=txtPersonelEkleEPosta.text.toString()
                    val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
                    val personelcursor=Database.rawQuery("SELECT Personel_EPosta FROM personeller WHERE Personel_EPosta=?",arrayOf(eposta))
                    if(personelcursor.count>0)
                    {
                        alert.setTitle("Hata")
                        alert.setMessage("Bu E-Mail adresi personeller tablosunda kayıtlı görünmektedir.")
                        alert.setPositiveButton("Tamam"){dialog,which->}
                    }
                    else
                    {
                        alert.setTitle("Emin misiniz?")
                        val isim=txtPersonelEkleİsim.text.toString()
                        val soyisim=txtPersonelEkleSoyisim.text.toString()
                        alert.setMessage("${isim} ${soyisim} ${seciliotopark}'a çalışan olarak ekleniyor.\nOnaylıyor musunuz?")
                        alert.setPositiveButton("Evet"){dialog,which->
                            Database.execSQL("INSERT INTO personeller(Otopark_ID,Personel_Adı,Personel_Soyadı,Personel_Telefon,Personel_EPosta,Personel_Sifre) VALUES(?,?,?,?,?,?)",
                                arrayOf(seciliotoparkid,
                                txtPersonelEkleİsim.text.toString(),
                                txtPersonelEkleSoyisim.text.toString(),
                                txtPersonelEkleTelefon.text.toString(),
                                txtPersonelEkleEPosta.text.toString(),
                                txtPersonelEkleSifre.text.toString()))
                            Toast.makeText(applicationContext,"Personel başarıyla eklendi",Toast.LENGTH_LONG).show()
                            val intent= Intent(applicationContext,PersonelSayfa::class.java)
                            startActivity(intent)
                            finish()
                        }
                        alert.setNegativeButton("Hayır"){dialog,which->}
                    }
                    alert.show()
                }
                catch (e:java.lang.Exception)
                {
                    Toast.makeText(applicationContext,"Bir hata meydana geldi",Toast.LENGTH_LONG).show()
                }
            }
            else
            {
                Toast.makeText(applicationContext,"Şifreler birbirleri ile uyuşmuyor\nTekrar giriniz",Toast.LENGTH_LONG).show()
                txtPersonelEkleSifreTekrar.setText("")
                txtPersonelEkleSifre.setText("")
            }
        }
        else
        {
            Toast.makeText(applicationContext,"Tüm verilerin eksiksiz olduğundan emin olunuz",Toast.LENGTH_LONG).show()
        }
    }

    fun Otoparklar()
    {
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val otoparkcursor=Database.rawQuery("SELECT * FROM otoparklar",null)
        if(otoparkcursor.count>0)
        {
            while(otoparkcursor.moveToNext())
            {
                Otoparklar.add(otoparkcursor.getString(otoparkcursor.getColumnIndex("Otopark_Adı").toInt()))
                OtoparklarID.add(otoparkcursor.getInt(otoparkcursor.getColumnIndex("Otopark_ID").toInt()))
            }
            val adapter= ArrayAdapter(this,R.layout.spinnerselectitem,Otoparklar)
            adapter.setDropDownViewResource(R.layout.spinnerdropitem)
            SpnPersonelEkleOtopark.adapter=adapter
        }
    }
}