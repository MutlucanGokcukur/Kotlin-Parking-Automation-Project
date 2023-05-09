package com.example.otoparkotomasyon

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import java.time.LocalDateTime


class QRGiris : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private var ID:Int=0
    private lateinit var Liste:ArrayList<String>
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrgiris)
        Liste=ArrayList<String>()
        val Database=openOrCreateDatabase("OtomasyonVeriTabanı", MODE_PRIVATE,null)
        ID=intent.getIntExtra("ID",0)

        val scannerView=findViewById<CodeScannerView>(R.id.qr_scanner)
        if(checkSelfPermission(android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            val permissionlist= arrayOf(Manifest.permission.CAMERA)
            requestPermissions(permissionlist,1)
        }
        //https://www.youtube.com/watch?v=6OFniVVzmgQ
        codeScanner=CodeScanner(this,scannerView)
        codeScanner.camera=CodeScanner.CAMERA_BACK
        codeScanner.formats=CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode= AutoFocusMode.SAFE
        codeScanner.isAutoFocusEnabled=true
        codeScanner.isFlashEnabled=false
        codeScanner.decodeCallback= DecodeCallback{
            runOnUiThread{
                //https://www.youtube.com/watch?v=7-d5mKjAJxc
                    val tarih=LocalDateTime.now().toLocalDate().toString()
                    val NetSaat=LocalDateTime.now()
                    var Saat=""
                    if(NetSaat.minute.toString().length==1)
                    {
                        Saat=NetSaat.hour.toString()+":0"+NetSaat.minute.toString()
                    }
                    else
                    {
                        Saat=NetSaat.hour.toString()+":"+NetSaat.minute.toString()
                    }

                    val QRKod=it.text.split("\n")
                    //Otopark_ID=1
                    //Park Edilen Blok:A
                    //Blok Numarası:5

                    val otoparkid=QRKod[0].get(QRKod[0].lastIndex)      //Son değeri alma (1)
                    var blok=QRKod[1].get(QRKod[1].lastIndex)           //Son değeri alma (A)
                    var numara=QRKod[2].get(QRKod[2].lastIndex)         //Son değeri alma (5)
                    var plaka=""
                    val mustericursor=Database.rawQuery("SELECT * FROM araclar WHERE Musteri_ID=?",arrayOf(ID.toString()))
                    if(mustericursor.count>0)
                    {
                        while (mustericursor.moveToNext())
                        {
                            mustericursor.moveToFirst()
                            plaka=mustericursor.getString(mustericursor.getColumnIndex("Arac_Plaka").toInt()).toString()
                        }
                    }
                    var doluotoid=0
                    var dolublok=""
                    var dolunumara=0
                    val kodcursor=Database.rawQuery("SELECT Otopark_ID,Arac_Blok,Blok_Numara FROM park_halindeki_araclar",null)
                    if(kodcursor.count>0)
                    {
                        while (kodcursor.moveToNext())
                        {
                            doluotoid=kodcursor.getInt(kodcursor.getColumnIndex("Otopark_ID").toInt())
                            dolublok=kodcursor.getString(kodcursor.getColumnIndex("Arac_Blok").toInt())
                            dolunumara=kodcursor.getInt(kodcursor.getColumnIndex("Blok_Numara").toInt())
                        }
                        if(otoparkid.toString()==doluotoid.toString())
                        {
                            if(blok.toString()==dolublok.toString()&&numara.toString()==dolunumara.toString())
                            {
                                val alert=AlertDialog.Builder(this)
                                alert.setTitle("Hata")
                                alert.setMessage("Burası sistemde dolu görünmektedir\nLütfen personel ile iletişime geçiniz.")
                                alert.setPositiveButton("Tamam"){dialog,which->
                                    val intent=Intent(applicationContext,UyeAnaSayfa::class.java)
                                    intent.putExtra("ID",ID)
                                    startActivity(intent)
                                    finish()
                                }
                                alert.show()
                            }
                            else
                            {
                                try {
                                    Database.execSQL("INSERT INTO park_halindeki_araclar VALUES(?,?,?,?,?,?)",
                                        arrayOf(plaka,otoparkid,tarih,Saat,blok,numara))
                                    val intent=Intent(applicationContext,UyeAnaSayfa::class.java)
                                    Toast.makeText(applicationContext,"Park etme işlemi başarıyla tamamlandı.",Toast.LENGTH_LONG).show()
                                    intent.putExtra("ID",ID)
                                    startActivity(intent)
                                    finish()
                                }
                                catch (e:java.lang.Exception)
                                {
                                    Toast.makeText(applicationContext,"Park etme işlemi tamamlanamadı.",Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                else
                    {
                        try {
                            Database.execSQL("INSERT INTO park_halindeki_araclar VALUES(?,?,?,?,?,?)",
                                arrayOf(plaka,otoparkid,tarih,Saat,blok,numara))
                            val intent=Intent(applicationContext,UyeAnaSayfa::class.java)
                            Toast.makeText(applicationContext,"Park etme işlemi başarıyla tamamlandı.",Toast.LENGTH_LONG).show()
                            intent.putExtra("ID",ID)
                            startActivity(intent)
                            finish()
                        }
                        catch (e:java.lang.Exception)
                        {
                            Toast.makeText(applicationContext,"Park etme işlemi tamamlanamadı.",Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
        codeScanner.errorCallback= ErrorCallback {
            runOnUiThread{
                Toast.makeText(this,"Hata", Toast.LENGTH_LONG).show()
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }
    override fun onResume()
    {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}