#!/bin/sh
#
# SPDX-FileCopyrightText: The LineageOS Project
# SPDX-License-Identifier: Apache-2.0
#

if [ -z "${OUT}" ]; then
    echo "\$OUT is empty. Is a device lunched?"
    exit 0
fi

if [ ! -f ~/Android/Sdk/platforms/android-35/android.jar ]; then
    echo "Error: ~/Android/Sdk/platforms/android-35/android.jar does not exist. Update SDK path."
    exit 1
fi

if [ ! -f "$OUT/../../common/obj/JAVA_LIBRARIES/framework-minus-apex_intermediates/classes-header.jar" ]; then
    echo "Error: classes-header.jar does not exist. Build it with 'make framework-minus-apex'."
    exit 1
fi

unzip -qq ~/Android/Sdk/platforms/android-35/android.jar -d /tmp/androidjar/
unzip -qq -o $OUT/../../common/obj/JAVA_LIBRARIES/framework-minus-apex_intermediates/classes-header.jar -d /tmp/androidjar/

jar cvf libs/android.jar -C /tmp/androidjar/ . &> /dev/null

rm -rf /tmp/androidjar/

echo "android.jar generated to libs"
