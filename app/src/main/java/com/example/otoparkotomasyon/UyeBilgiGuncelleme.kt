package com.example.otoparkotomasyon

import android.content.Intent
import android.graphics.ColorSpace.Model
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_uye_bilgi_guncelleme.*
import kotlinx.android.synthetic.main.activity_uye_olma.*

class UyeBilgiGuncelleme : AppCompatActivity() {
    private var ID:Int=0
    private lateinit var Markalar:MutableList<String>
    private lateinit var Modeller:MutableList<String>
    private lateinit var secilimarka:String
    private lateinit var secilimodel:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uye_bilgi_guncelleme)
        secilimarka=""
        Markalar= mutableListOf<String>()
        Modeller= mutableListOf<String>()
        //region Verileri Kapatma

        txtUyeBilgiGuncellemeİsim.isEnabled=false
        txtUyeBilgiGuncellemeSoyisim.isEnabled=false
        txtUyeBilgiGuncellemeTelefon.isEnabled=false
        txtUyeBilgiGuncellemeEPosta.isEnabled=false

        txtUyeBilgiGuncellemeMarka.isEnabled=false
        txtUyeBilgiGuncellemeModel.isEnabled=false

        txtUyeBilgiGuncellemeSifre.isEnabled=false
        txtUyeBilgiGuncellemePlaka.isEnabled=false
        ChcUyeBilgiGuncellemeBir.isEnabled=false
        ChcUyeBilgiGuncellemeİki.isEnabled=false

        btnUyeBilgileriGuncelle.isVisible=false


        //endregion
        ID=intent.getIntExtra("ID",0)

        VerileriGetir()

    }

    fun VerileriGetir()
    {
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        val cursor=Database.rawQuery("SELECT * FROM musteriler INNER JOIN araclar ON araclar.Musteri_ID=musteriler.Musteri_ID INNER JOIN markalar ON markalar.Arac_ID=araclar.Arac_ID INNER JOIN modeller ON modeller.Arac_ID=markalar.Arac_ID WHERE musteriler.Musteri_ID=?", arrayOf(ID.toString()))

        while(cursor.moveToNext())
        {
            txtUyeBilgiGuncellemeİsim.setText(cursor.getString(cursor.getColumnIndex("Adı").toInt()).toString())
            txtUyeBilgiGuncellemeSoyisim.setText(cursor.getString(cursor.getColumnIndex("Soyadı").toInt()).toString())
            txtUyeBilgiGuncellemeTelefon.setText(cursor.getInt(cursor.getColumnIndex("Telefon_Numara").toInt()).toString())
            txtUyeBilgiGuncellemeEPosta.setText(cursor.getString(cursor.getColumnIndex("EPosta").toInt()).toString())
            txtUyeBilgiGuncellemeSifre.setText(cursor.getString(cursor.getColumnIndex("Sifre").toInt()).toString())

            txtUyeBilgiGuncellemeMarka.setText(cursor.getString(cursor.getColumnIndex("Araba_Markası").toInt()).toString())
            txtUyeBilgiGuncellemeModel.setText(cursor.getString(cursor.getColumnIndex("Model_Adı").toInt()).toString())

            txtUyeBilgiGuncellemePlaka.setText(cursor.getString(cursor.getColumnIndex("Arac_Plaka").toInt()).toString())
            val aracsınıf=cursor.getInt(cursor.getColumnIndex("Arac_Sınıf").toInt())
            if(aracsınıf==1)
            {
                ChcUyeBilgiGuncellemeBir.isChecked=true
            }
            else if(aracsınıf==2)
            {
                ChcUyeBilgiGuncellemeİki.isChecked=true
            }
        }
    }
    fun Guncelleme()
    {
        val alert=AlertDialog.Builder(this)
        alert.setTitle("Emin misiniz?")
        alert.setMessage("Bilgileriniz güncellenecektir\nVerilerinizin doğruluğundan emin misiniz?")
        alert.setPositiveButton("Evet"){dialog,which->
            try
            {
                val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
                Database.execSQL("UPDATE musteriler SET Adı=?,Soyadı=?,Telefon_Numara=?,EPosta=?,Sifre=? WHERE Musteri_ID=${ID}",
                    arrayOf(txtUyeBilgiGuncellemeİsim.text.toString(),
                        txtUyeBilgiGuncellemeSoyisim.text.toString(),
                        txtUyeBilgiGuncellemeTelefon.text.toString().toInt(),
                        txtUyeBilgiGuncellemeEPosta.text.toString(),
                        txtUyeBilgiGuncellemeSifre.text.toString()
                    )
                )
                val aracid=Database.rawQuery("SELECT Arac_ID from markalar WHERE Araba_Markası=?", arrayOf(secilimarka))
                var id=0
                while(aracid.moveToNext())
                {
                    id=aracid.getInt(aracid.getColumnIndex("Arac_ID").toInt())
                }
                Database.execSQL("UPDATE araclar SET Arac_Plaka=?,Arac_ID=? WHERE Musteri_ID=?", arrayOf(txtUyeBilgiGuncellemePlaka.text.toString(),id,ID))
                if (ChcUyeBilgiGuncellemeBir.isChecked)
                {
                    Database.execSQL("UPDATE araclar SET Arac_Sınıf=1 WHERE Musteri_ID=${ID}")
                }
                else if (ChcUyeBilgiGuncellemeİki.isChecked)
                {
                    Database.execSQL("UPDATE araclar SET Arac_Sınıf=2 WHERE Musteri_ID=${ID}")
                }
                Toast.makeText(applicationContext,"Güvenlik sebebi ile hesaptan çıkartılıyorsunuz\nTekrar giriş yapınız",Toast.LENGTH_LONG).show()
                val intent= Intent(applicationContext,UyeGiris::class.java)
                startActivity(intent)
                finish()
            }
            catch (e:Exception)
            {
                Toast.makeText(applicationContext,"Güncelleme Hatası",Toast.LENGTH_LONG).show()
            }
        }
        alert.setNegativeButton("Hayır"){dialog,which->
        }
        alert.show()
    }

    fun BilgileriGuncelle(view:View)
    {
        Guncelleme()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.guncellemeacma,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Markalar.clear()
        Modeller.clear()
        if(item.itemId==R.id.guncellemeacma)
        {
            val alert=AlertDialog.Builder(this)
            alert.setTitle("Emin misiniz?")
            alert.setMessage("Bilgilerinizi güncelleme şu an kapalı durumda\nGüncelleme panelini açmak istediğinizden emin misiniz?")
            alert.setPositiveButton("Evet"){dialog,which->
                //region Veri giriş açma
                txtUyeBilgiGuncellemeİsim.isEnabled=true
                txtUyeBilgiGuncellemeSoyisim.isEnabled=true
                txtUyeBilgiGuncellemeTelefon.isEnabled=true
                txtUyeBilgiGuncellemeEPosta.isEnabled=true

                txtUyeBilgiGuncellemeMarka.setText(" ")
                txtUyeBilgiGuncellemeModel.setText(" ")

                txtUyeBilgiGuncellemeSifre.isEnabled=true
                txtUyeBilgiGuncellemePlaka.isEnabled=true
                ChcUyeBilgiGuncellemeBir.isEnabled=true
                ChcUyeBilgiGuncellemeİki.isEnabled=true

                btnUyeBilgileriGuncelle.isVisible=true

                //endregion

                val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)

                val markacursor=Database.rawQuery("Select Araba_Markası from markalar",null)
                while(markacursor.moveToNext())
                {
                    Markalar.add(markacursor.getString(markacursor.getColumnIndex("Araba_Markası").toInt()).toString())
                }
                SpnUyeBilgiGuncellemeMarka.onItemSelectedListener=object :AdapterView.OnItemSelectedListener
                {
                    override fun onItemSelected (p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        secilimarka=Markalar.get(p2)    //Hangi marka seçildi
                        Modeller()
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?)
                    {
                        Toast.makeText(applicationContext,"Lütfen bir marka seçiniz", Toast.LENGTH_LONG).show()
                    }
                }
                val markaadapter=ArrayAdapter(this,R.layout.spinnerselectitem,Markalar)
                markaadapter.setDropDownViewResource(R.layout.spinnerdropitem)
                SpnUyeBilgiGuncellemeMarka.adapter=markaadapter

            }
            alert.setNegativeButton("Hayır"){dialog,which->}
            alert.show()
        }
        return super.onOptionsItemSelected(item)
    }
    fun Modeller()
    {
        Modeller.clear()
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)

            val modelcursor=Database.rawQuery("SELECT Model_Adı from modeller Where Arac_ID=(SELECT Arac_ID from markalar WHERE Araba_Markası=?)", arrayOf(secilimarka))
            while(modelcursor.moveToNext())
            {
                Modeller.add(modelcursor.getString(modelcursor.getColumnIndex("Model_Adı").toInt()).toString())
            }

            SpnUyeBilgiGuncellemeModel.onItemSelectedListener=object :AdapterView.OnItemSelectedListener
            {
                override fun onItemSelected (p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    secilimodel=Modeller.get(p2)    //Hangi marka seçildi

                }
                override fun onNothingSelected(p0: AdapterView<*>?)
                {
                    Toast.makeText(applicationContext,"Lütfen bir marka seçiniz", Toast.LENGTH_LONG).show()
                }
            }
            val modeladapter=ArrayAdapter(this,R.layout.spinnerselectitem,Modeller)
            modeladapter.setDropDownViewResource(R.layout.spinnerdropitem)
            SpnUyeBilgiGuncellemeModel.adapter=modeladapter
    }
}