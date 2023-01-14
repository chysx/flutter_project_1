### 补丁文件相关
1. 补丁文件夹：data/data/packageName/file/patch/
2. 补丁文件：libapp.so，patch_info.txt


### 补丁信息PatchInfo包含如下内容：
1. baseApkCode: apk基准版本号
2. versionCode: 补丁版本号
3. versionName:补丁名称
4. des:补丁描述信息
5. md5: 补丁md5校验码
6. downloadUrl：补丁下载路径

### 补丁检测、下载、加载逻辑

#### 补丁检测
1. 在flutter侧调用接口patchCheck，返回数据为补丁信息PatchInfo
2. If 获取成功： 通知native侧并传递补丁信息patchInfo，然后native侧开启下载补丁服务
3. else 获取失败，不做任何处理

#### 补丁下载服务：
首先校验该补丁的合法性：
1. 当前运行的apk版本 = （flutter侧传来的）baseApkCode
2. 当前运行的apk中的补丁版本号（通过查询patchInfo.txt） 《  versionCode
3. 当前运行的apk中的补丁MD5值 ！= （flutter侧传来的）md5
4. 如果合法就开始下载

#### 下载补丁：
1. 开始下载之前，首先检测补丁文件夹xxx/patch是否存在，如果不存在就mkdir,然后新建临时文件patchInfoTemp.txt,并且将flutter侧传来的补丁信息写入到该文件中
2. 下载过程中，新建临时补丁文件patchTemp.wy
3. 下载成功后，
a. 首先校验md5，（flutter侧传来的）md5 = 补丁文件patchTemp.wy的md5
如果校验成功
将patchInfo.txt文件删除，将patchInfoTemp.txt文件重命名为patchInfo.txt，将libapp.so文件删除，将patchTemp.wy重命名为libapp.so
如果校验失败
将patchInfoTemp.txt和patchTemp.wy文件删除


#### 加载补丁：
加载补丁的条件：
补丁文件夹xxx/patch存在
if (patchInfo.txt文件存在，并且 libapp.so文件存在）{
删除patchInfoTemp.txt文件（如果有的话，概率较小），删除patchTemp.wy文件（如果有的话，概率较小），
（*******注意******以下任何一个校验不通过，都需要清空补丁文件夹xxx/patch）
当前运行的apk版本 = （通过查询patchInfo.txt）baseApkCode
校验md5，（通过查询patchInfo.txt）md5 = 补丁文件libapp.so的md5 (这是一个双重检测的机制，如果比较耗时，也可以不执行这一步)

	如果都校验通过，那么就可以加载补丁文件了，否则的话都需要清空补丁文件夹xxx/patch

}else{
清空补丁文件夹xxx/patch
}
