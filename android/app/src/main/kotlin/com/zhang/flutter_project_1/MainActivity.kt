package com.zhang.flutter_project_1

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterShellArgs

class MainActivity: FlutterActivity() {
    override fun getFlutterShellArgs(): FlutterShellArgs {
        val flutterShellArgs = super.getFlutterShellArgs()
        if(FileUtil.isHas(this, "solib")){
            val soPath = filesDir.absolutePath + "/solib/" + "libapp.so"
            flutterShellArgs.add("--aot-shared-library-name=$soPath")
        }
        return flutterShellArgs
    }
}
