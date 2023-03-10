package com.zhang.flutter_project_1;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Field;

import io.flutter.FlutterInjector;
import io.flutter.embedding.engine.loader.FlutterApplicationInfo;
import io.flutter.embedding.engine.loader.FlutterLoader;


/**
 * 利用反射把tinker和sophix生成的flutter补丁，打进flutter的so加载流程中
 *
 */
public class FlutterPatch {

    private static final String TAG = "Tinker";
    private static String libPathFromSophix = "";

    private static boolean isUseSophix = false;
    private static boolean isUseTinker = false;


    private FlutterPatch() {
    }

    public static String getLibPath(Context context, String abis) {
//        String libPath = findLibraryFromTinker(context, "lib" + File.separator + getCpuABI(abis), "libapp.so");
//        if (!TextUtils.isEmpty(libPath) && libPath.equals("libapp.so")) {
//            return null;
//        }
//        return libPath;
        return "";
    }


    public static void reflect(String libPath) {
        try {
            FlutterLoader flutterLoader = FlutterInjector.instance().flutterLoader();

            Field field = FlutterLoader.class.getDeclaredField("flutterApplicationInfo");
            field.setAccessible(true);

            FlutterApplicationInfo flutterApplicationInfo = (FlutterApplicationInfo) field.get(flutterLoader);

            Field aotSharedLibraryNameField = FlutterApplicationInfo.class.getDeclaredField("aotSharedLibraryName");
            aotSharedLibraryNameField.setAccessible(true);
            aotSharedLibraryNameField.set(flutterApplicationInfo, libPath);

            field.set(flutterLoader, flutterApplicationInfo);

            TinkerLog.i(TAG, "flutter patch is loaded successfully");

        } catch (Exception e) {
            TinkerLog.i(TAG, "flutter reflect is failed");
            e.printStackTrace();
        }
    }

    /**
     *
     * 插桩方法
     * 此方法不可修改，否则不会成功
     *
     * Tinker和Sophix都集成这种情况是不可能发生的吧？
     *
     * @param obj
     * @param abis 从gradle里的ndk读取配置
     *
     */
    public static void hook(Object obj, Object abis) {
        if (obj instanceof Context) {

            Context context = (Context) obj;
            String abisStr = String.valueOf(abis);
            TinkerLog.i(TAG, "find FlutterMain");

            if (isUseTinker) {

                String libPathFromTinker = getLibPath(context, abisStr);
                if (!TextUtils.isEmpty(libPathFromTinker)) {
                    reflect(libPathFromTinker);
                }
            } else if (isUseSophix) {

                if (!TextUtils.isEmpty(libPathFromSophix)) {
                    reflect(libPathFromSophix);
                }
            } else {
                TinkerLog.i(TAG, "lib path is null");
            }

        } else {

            TinkerLog.i(TAG, "Object: " + obj.getClass().getName());
        }

    }

    /**
     * Sophix 插桩方法，获取sophix so包路径
     *
     * 此方法不可修改，否则不会成功
     * @param obj
     */
    public static void hookSophix(Object obj) {

        if (null != obj) {
            File file = new File(obj.toString()+"/libs/libapp.so");
            if (file.exists() && !file.isDirectory()) {
                libPathFromSophix = file.getAbsolutePath();
                TinkerLog.i(TAG, "path is " + libPathFromSophix);
            } else {
                TinkerLog.i(TAG, "path file is not exist");
            }
        }
    }

    /**
     * Sophix 插桩方法，获取项目是否使用sophix
     *
     * 此方法不可修改，否则不会成功
     */
    public static void hookIsUseSophix() {
        isUseSophix = true;
        TinkerLog.i(TAG, "is use sophix");
    }

    /**
     * Sophix 插桩方法，获取项目是否使用Tinker
     *
     * 此方法不可修改，否则不会成功
     */
    public static void hookIsUseTinker() {
        isUseTinker = true;
        TinkerLog.i(TAG, "is use tinker");
    }


    /**
     * 获取最优abi
     * 先判断ndk是否配置了，未配置情况还按最优走
     *
     *
     * 当单独配置一个abi时，按此abi处理
     * 配置多个abi，先查最优abi是否在里面，在就用最优，
     * 不在里面用随机（此处随机是指读取的ndk配置是无序的，所以就是取第一个也不一定是build.gradle里配置的第一个）一个配置
     *
     * @return
     */
    public static String getCpuABI(String abis) {
        TinkerLog.i(TAG, "all ndk config >> " + abis);
        String abi = "";

        if (Build.VERSION.SDK_INT >= 21) {
            for (String cpu : Build.SUPPORTED_ABIS) {
                if (!TextUtils.isEmpty(cpu)) {
                    abi = cpu;
                    break;
                }
            }
        } else {
            abi = Build.CPU_ABI;
        }


        if (TextUtils.isEmpty(abis)) {
            TinkerLog.i(TAG, "cpu abi is:" + abi);
            return abi;
        } else {

            String[] abiStrs = abis.split(",");

            if (abiStrs.length > 0) {
                // 只有一个直接取，多个先看是否有最优abi，有就用，没有就取第一个
                 if (abiStrs.length == 1) {

                    abi = abiStrs[0];

                    TinkerLog.i(TAG, "cpu abi is:" + abi + " from ndk config");
                    return abi;

                 } else {

                     for (String abiStr : abiStrs) {
                         if (abiStr.equals(abi)) {

                             TinkerLog.i(TAG, "cpu abi is:" + abi);
                             return abi;
                         }
                     }
                     abi = abiStrs[0];

                     TinkerLog.i(TAG, "cpu abi is:" + abi + " from ndk config");
                     return abi;
                 }
            }
        }
        return "";
    }
}