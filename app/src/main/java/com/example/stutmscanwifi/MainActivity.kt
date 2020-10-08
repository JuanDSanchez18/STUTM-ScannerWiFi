package com.example.stutmscanwifi

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val runningQOrLater =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private lateinit var wifiManager: WifiManager

    private var timer = Timer()

    private val wifiScanReceiver = object : BroadcastReceiver() {


        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                scanSuccess()
            } else {
                scanFailure()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (checkPermissions()) {
            scanReport.append("Check permissions")

        }

        scanBtn.setOnClickListener{
            corutineScanWifi()

        }
        stopScanBtn.setOnClickListener{
            timer.cancel()
            scanReport.text = ""
            scanReport.append("Finish Scan")
        }
    }


    private fun checkPermissions(): Boolean {
        if (runningQOrLater ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                21
            )
            val permissionAccessFineLocationApproved = ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
            val permissionAccessBackgroundLocationApproved = ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

            return permissionAccessFineLocationApproved && permissionAccessBackgroundLocationApproved


        }else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                22
            )

            return ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    /*private fun corutineScanWifi() {

        val timer = object: CountDownTimer(300000, 15000) {
            override fun onTick(millisUntilFinished: Long) {
                // do something
                scanWifiNetworks()
            }
            override fun onFinish() {
                // do something
                scanReport.text = ""
                scanReport.append("Finish 5 minutes")
            }
        }
        timer.start()
    }*/

    private fun corutineScanWifi() {

        timer = Timer()
        //Set the schedule function
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    // Magic here
                    scanWifiNetworks()
                }
            },
            0, 20000
        )
    }


    private fun scanWifiNetworks() {
//https://github.com/shmulman/WifiSense_v5/blob/master/app/src/main/java/il/co/shmulman/www/wifisense_v5/MainActivity.kt
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)

        val success = wifiManager.startScan()
        if (!success) {
            // scan failure handling

            scanFailure()
        }

    }

    private fun scanFailure() {
        scanReport.text = ""
        scanReport.append("Scan fail")

    }

    private fun scanSuccess() {
        val results = wifiManager.scanResults
        scanReport.text = "Scan WiFi success" + "\n"
        scanReport.append("Number of access point: ")
        scanReport.append(results.size.toString() + "\n")
        //... use new scan results ...
        /*for (result in results){
            //Toast.makeText(applicationContext, result.SSID, Toast.LENGTH_SHORT).show()
            scanReport.append(result.SSID + " " + result.level + " dBm " + " " + result.BSSID + "\n")

        }*/

    }

}