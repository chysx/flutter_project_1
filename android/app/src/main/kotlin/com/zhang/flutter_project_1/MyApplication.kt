package com.zhang.flutter_project_1
import io.flutter.app.FlutterApplication
import io.flutter.embedding.engine.loader.FlutterLoader
import io.flutter.view.FlutterMain
import java.io.File

class MyApplication : FlutterApplication() {

    override fun onCreate() {
        super.onCreate()
        if(FileUtil.isHas(this, "solib")){
//            FlutterMain.startInitialization(this)
            println("**********11111111111")
            val soPath = getSoPath()
//            FlutterPatch.reflect(soPath)
        }
    }

     fun getSoPath(): String {
        val assetsPath = "solib"
        val soDic = File(filesDir.absolutePath + "/solib")
        if (!soDic.exists()) {
            soDic.mkdirs()
        }
        if (soDic.list()?.isEmpty() == true) {
            FileUtil.doCopy(this, assetsPath, soDic.absolutePath)
            println("**********2222222222")
        }
        return filesDir.absolutePath + "/solib/" + "libapp.so"
    }
}