package com.example.otoparkotomasyon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_uye_olma.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.ContactsContract.Data
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class UyeOlma : AppCompatActivity() {
    private lateinit var markalar:MutableList<String>
    private lateinit var modeller:MutableList<String>
    private lateinit var MarkaDurum:SharedPreferences
    private lateinit var ModelDurum:SharedPreferences
    private lateinit var secilimarka: String
    private lateinit var secilimodel:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uye_olma)
        title="Üye Olma"
        MarkaDurum=this.getSharedPreferences("com.example.otoparkotomasyon",Context.MODE_PRIVATE)
        ModelDurum=this.getSharedPreferences("com.example.otoparkotomasyon",Context.MODE_PRIVATE)
        markalar= mutableListOf<String>()
        modeller= mutableListOf<String>()
        Markalar()
        SpnUyeOlmaArabaMarkaları.onItemSelectedListener=object :AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected (p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                secilimarka=markalar.get(p2)    //Hangi marka seçildi
                Modeller(secilimarka)
            }
            override fun onNothingSelected(p0: AdapterView<*>?)
            {
                Toast.makeText(applicationContext,"Lütfen bir marka seçiniz", Toast.LENGTH_LONG).show()
            }
        }

        SpnUyeOlmaModeller.onItemSelectedListener=object :AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected (p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                secilimodel=modeller.get(p2)    //Hangi marka seçildi
            }
            override fun onNothingSelected(p0: AdapterView<*>?)
            {
                Toast.makeText(applicationContext,"Lütfen bir model seçiniz", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun Markalar()
    {
        var MarkalarDatabase=this.openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        MarkalarDatabase.execSQL("CREATE TABLE IF NOT EXISTS markalar(Arac_ID INTEGER PRIMARY KEY,Araba_Markası VARCHAR)")

        if(MarkaDurum.getString("MarkaDurum","null")=="null")
        {
            MarkalarDatabase.execSQL("INSERT INTO markalar VALUES(1,'Audi')")
            MarkalarDatabase.execSQL("INSERT INTO markalar VALUES(2,'BMW')")
            MarkalarDatabase.execSQL("INSERT INTO markalar VALUES(3,'Fiat')")
            MarkalarDatabase.execSQL("INSERT INTO markalar VALUES(4,'Renault')")
            MarkalarDatabase.execSQL("INSERT INTO markalar VALUES(5,'Volkswagen')")
            MarkaDurum.edit().putString("MarkaDurum","1").apply()
        }
        val cursor = MarkalarDatabase.rawQuery("SELECT * FROM markalar", null)
        while(cursor.moveToNext())
        {
            val marka=cursor.getString(cursor.getColumnIndex("Araba_Markası").toInt())
            markalar.add(marka)
        }
        //https://www.youtube.com/watch?v=N8GfosWTt44
        val adapter=ArrayAdapter(this,R.layout.spinnerselectitem,markalar)
        adapter.setDropDownViewResource(R.layout.spinnerdropitem)
        SpnUyeOlmaArabaMarkaları.adapter=adapter
    }

    fun Modeller(secilimarka:String) {
        modeller.clear()
        val ModellerDatabase = this.openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE, null)
        ModellerDatabase.execSQL("CREATE TABLE IF NOT EXISTS modeller(Arac_ID INTEGER, Model_Adı VARCHAR, FOREIGN KEY(Arac_ID) REFERENCES markalar(Arac_ID))")

        if(ModelDurum.getString("ModelDurum","null")=="null")
        {
            ModellerDatabase.execSQL("INSERT INTO modeller (Arac_ID, Model_Adı) VALUES (1, 'A Serisi')")
            ModellerDatabase.execSQL("INSERT INTO modeller (Arac_ID, Model_Adı) VALUES (1, 'Q Serisi')")
            ModellerDatabase.execSQL("INSERT INTO modeller (Arac_ID, Model_Adı) VALUES (1, 'E-Tron')")

            ModellerDatabase.execSQL("INSERT INTO modeller (Arac_ID, Model_Adı) VALUES (2, 'I Serisi')")
            ModellerDatabase.execSQL("INSERT INTO modeller (Arac_ID, Model_Adı) VALUES (2, 'X Serisi')")
            ModellerDatabase.execSQL("INSERT INTO modeller (Arac_ID, Model_Adı) VALUES (2, 'M Serisi')")

            ModellerDatabase.execSQL("INSERT INTO modeller (Arac_ID, Model_Adı) VALUES (3, 'Egea')")
            ModellerDatabase.execSQL("INSERT INTO modeller (Arac_ID, Model_Adı) VALUES (3, 'Panda')")
            ModellerDatabase.execSQL("INSERT INTO modeller (Arac_ID, Model_Adı) VALUES (3, '500')")

            ModellerDatabase.execSQL("INSERT INTO modeller (Arac_ID, Model_Adı) VALUES (4, 'Clio')")
            ModellerDatabase.execSQL("INSERT INTO modeller (Arac_ID, Model_Adı) VALUES (4, 'Megane')")
            ModellerDatabase.execSQL("INSERT INTO modeller (Arac_ID, Model_Adı) VALUES (4, 'Fluence')")

            ModellerDatabase.execSQL("INSERT INTO modeller (Arac_ID, Model_Adı) VALUES (5, 'Polo')")
            ModellerDatabase.execSQL("INSERT INTO modeller (Arac_ID, Model_Adı) VALUES (5, 'Golf')")
            ModellerDatabase.execSQL("INSERT INTO modeller (Arac_ID, Model_Adı) VALUES (5, 'Tiguan')")
            ModelDurum.edit().putString("ModelDurum","1").apply()
        }

        val markacursor=ModellerDatabase.rawQuery("SELECT Arac_ID from markalar where Araba_Markası=?",
            arrayOf(secilimarka)
        )
        var modelid=""
        while (markacursor.moveToNext())
        {
            modelid=markacursor.getString(markacursor.getColumnIndex("Arac_ID").toInt()).toString()
        }
        val modelcursor=ModellerDatabase.rawQuery("SELECT Model_Adı from modeller WHERE Arac_ID=?",
            arrayOf(modelid)
        )
        var model=""
        while(modelcursor.moveToNext())
        {
            model=modelcursor.getString(modelcursor.getColumnIndex("Model_Adı").toInt()).toString()
            modeller.add(model)
        }
        val adapter=ArrayAdapter(this,R.layout.spinnerselectitem,modeller)
        adapter.setDropDownViewResource(R.layout.spinnerdropitem)
        SpnUyeOlmaModeller.adapter=adapter
    }

    fun HesapOlustur(view:View) {
        val alert = AlertDialog.Builder(this)
        val Database = openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE, null)
        if (txtUyeOlmaSifre.text.toString() != "" && txtUyeOlmaSifreTekrar.text.toString() != "") {
            try
            {
                val sorgu = Database.rawQuery("SELECT * FROM musteriler WHERE EPosta=?", arrayOf(txtUyeOlmaEMailAdres.text.toString()))

                if (sorgu.count>0) {

                    alert.setTitle("Hata!!")
                    alert.setMessage("Girdiğiniz E-Mail sistemimizde kayıtlıdır.\nLütfen tekrar giriş yapınız")
                    alert.setPositiveButton("Tamam") { dialog, which ->
                        txtUyeOlmaEMailAdres.setText("")
                        alert.show()
                    }
                }
            }
            catch (e:java.lang.Exception)
            {

            }

                try {
                        var aracsınıf = 0
                        Database.execSQL("CREATE TABLE IF NOT EXISTS araclar(Musteri_ID INTEGER,Arac_Plaka VARCHAR PRIMARY KEY,Arac_ID INTEGER,Arac_Sınıf INT,FOREIGN KEY(Musteri_ID) REFERENCES musteriler(Musteri_ID),FOREIGN KEY(Arac_ID) REFERENCES markalar(Arac_ID))")
                        if (chcUyeOlmaBir.isChecked) {
                            aracsınıf = 1
                        } else if (chcUyeOlmaİki.isChecked) {
                            aracsınıf = 2
                        }
                        if (aracsınıf == 0) {
                            Toast.makeText(this, "Lütfen aracınızın sınıfını giriniz", Toast.LENGTH_LONG).show()
                        }
                            Database.execSQL(
                                "INSERT INTO musteriler(Adı,Soyadı,Telefon_Numara,EPosta,Sifre) VALUES(?,?,?,?,?)",
                                arrayOf(txtUyeOlmaİsim.text.toString(),
                                    txtUyeOlmaSoyisim.text.toString(),
                                    txtUyeOlmaTelefon.text.toString(),
                                    txtUyeOlmaEMailAdres.text.toString(),
                                    txtUyeOlmaSifre.text.toString()))
                            val sonuc = Database.rawQuery(
                                "SELECT * FROM musteriler WHERE EPosta=?",
                                arrayOf(txtUyeOlmaEMailAdres.text.toString()))

                        if (sonuc.count > 0) {
                            var aracid=0
                            val idcursor=Database.rawQuery("SELECT Arac_ID from markalar WHERE Araba_Markası=?",
                                arrayOf(secilimarka)
                            )
                            while(idcursor.moveToNext())
                            {
                                idcursor.moveToFirst()
                                aracid=idcursor.getInt(idcursor.getColumnIndex("Arac_ID").toInt()).toInt()
                            }
                            sonuc.moveToFirst()

                            val ID = sonuc.getInt(sonuc.getColumnIndex("Musteri_ID").toInt())
                            Database.execSQL(
                                "INSERT INTO araclar(Musteri_ID,Arac_Plaka,Arac_ID,Arac_Sınıf) VALUES(?,?,?,?)", arrayOf(
                                        ID,
                                        txtUyeOlmaPlaka.text.toString(),
                                        aracid,
                                        aracsınıf))
                        }
                    alert.setTitle("Tebrikler!!")
                    alert.setMessage("Üye olma işleminiz başarıyla gerçekleştirildi\nSizi Giriş sayfasına yönlendiriliyorum..")
                    alert.setPositiveButton("Tamam") { dialog, which ->
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    alert.show()
                } catch (e: java.lang.Exception) {
                    Toast.makeText(applicationContext,"Hata meydana geldi\nTekrar deneyiniz",Toast.LENGTH_LONG).show()
                }

        }
        else
        {
            alert.setTitle("Hata!!")
            alert.setMessage("Girdiğiniz şifreler birbiri ile uyuşmamaktadır.\nLütfen tekrar giriş yapınız")
            alert.setPositiveButton("Tamam") { dialog, which ->
                txtUyeOlmaSifre.setText("")
                txtUyeOlmaSifreTekrar.setText("")
            }
            alert.show()
        }
    }
}