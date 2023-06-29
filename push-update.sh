#!/bin/sh

updates_dir=/data/crdroid_updates

if [ ! -f "$1" ]; then
   echo "用法: $0 ZIP [未验证]"
   echo "将ZIP推送到$updates_dir并将其添加到更新器"
   echo
   echo "ZIP的名称应以crdroid-VERSION-DATE-TYPE-*格式命名"
   echo "如果设置了未验证选项，应用程序将验证更新"
   exit
fi
zip_path=`realpath "$1"`

if [ "`adb get-state 2>/dev/null`" != "device" ]; then
    echo "未找到设备。等待设备连接..."
    adb wait-for-device
fi
if ! adb root; then
    echo "无法以root身份运行adbd"
    exit 1
fi

zip_path_device=$updates_dir/`basename "$zip_path"`
if adb shell test -f "$zip_path_device"; then
    echo "$zip_path_device 已经存在"
    exit 1
fi

if [ -n "$2" ]; then
    status=1
else
    status=2
fi

# 假设 crdroid-VERSION-DATE-TYPE-*.zip
zip_name=`basename "$zip_path"`
id=`echo "$zip_name" | sha1sum | cut -d' ' -f1`
version=`echo "$zip_name" | cut -d'-' -f2`
type=`echo "$zip_name" | cut -d'-' -f4`
build_date=`echo "$zip_name" | cut -d'-' -f3 | cut -d'_' -f1`
if [ "`uname`" = "Darwin" ]; then
    timestamp=`date -jf "%Y%m%d %H:%M:%S" "$build_date 23:59:59" +%s`
    size=`stat -f%z "$zip_path"`
else
    timestamp=`date --date="$build_date 23:59:59" +%s`
    size=`stat -c "%s" "$zip_path"`
fi

adb push "$zip_path" "$zip_path_device"
adb shell chgrp cache "$zip_path_device"
adb shell chmod 664 "$zip_path_device"

# 更新数据库前先关闭应用程序
adb shell "killall com.crdroid.updater 2>/dev/null"
adb shell "sqlite3 /data/data/com.crdroid.updater/databases/updates.db" \
    "\"INSERT INTO updates (status, path, download_id, timestamp, type, version, size)" \
    "  VALUES ($status, '$zip_path_device', '$id', $timestamp, '$type', '$version', $size)\""

# 退出root模式
adb unroot