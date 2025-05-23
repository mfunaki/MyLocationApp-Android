package mayoct.net.mylocationapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var latitudeTextView: TextView
    private lateinit var longitudeTextView: TextView

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var requestingLocationUpdates = false

    // GoogleMapオブジェクトを保持する変数
    private var googleMap: GoogleMap? = null
    private var currentLatLng: LatLng? = null   // 現在の緯度軽度を保持

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        latitudeTextView = findViewById(R.id.latitudeTextView)
        longitudeTextView = findViewById(R.id.longitudeTextView)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // LocationRequestの作成と設定
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)   // 10秒ごと
            .setMinUpdateIntervalMillis(5000)   // 最短5秒ごと
            .build()

        // LocationCallbackの作成
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                // locationResult.locations はリストなので、最新の位置情報を取得
                locationResult.lastLocation?.let { location ->
                    val latitude = location.latitude
                    val longitude = location.longitude
                    latitudeTextView.text = "緯度: $latitude"
                    longitudeTextView.text = "経度 $longitude"
                    println("定期更新 - 緯度: $latitude, 経度: $longitude")

                    // 地図を更新
                    currentLatLng = LatLng(latitude, longitude)
                    updateMapLocation()
                }
            }
        }

        // SupportMapFragmentを取得し、地図の準備ができたら onMapReady を呼び出すように設定
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)   // this (MainActivity) がOnMapReadyCallbackを実装
    }

    // OnMapReadyCallbackメソッド: 地図の準備ができたら呼び出される
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // 地図の初期設定
        googleMap?.uiSettings?.isZoomControlsEnabled = true // ズームコントロールを表示

        // 最初に位置情報が取得できれいれば地図に表示
        currentLatLng?.let {
            updateMapLocation()
        }
    }

    // 地図上の位置を更新するメソッド
    private fun updateMapLocation() {
        googleMap?.let { map ->
            currentLatLng?.let { latLng ->
                map.clear() // 古いマーカーをクリア
                map.addMarker(MarkerOptions().position(latLng).title("現在地"))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))  // ズームレベル15で表示
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // パーミッションがなければリクエスト
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        // パーミッションがあれば更新を開始
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        requestingLocationUpdates = true
        println("位置情報の定期更新を開始しました。")
    }

    private fun stopLocationUpdates() {
        if (requestingLocationUpdates) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            requestingLocationUpdates = false
            println("位置情報の定期更新を停止しました。")
        }
    }

    override fun onResume() {
        super.onResume()
        if (!requestingLocationUpdates) {
            // パーミッションを確認してから位置情報更新を開始
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                // パーミッションがない場合、リクエストする
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()   // アプリがバックグラウンドになる際に更新を停止
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // 許可された場合
                Toast.makeText(this, "位置情報の許可が得られました。", Toast.LENGTH_SHORT).show()
            } else {
                // 拒否された場合
                latitudeTextView.text = "緯度: 許可なし"
                longitudeTextView.text = "経度: 許可なし"
                Toast.makeText(this, "位置情報の許可が拒否されました。", Toast.LENGTH_SHORT).show()
            }
        }
    }
}