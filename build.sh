set -e
cd /sdcard/AppProjects/Telecharbanque
rm -rf bin
mkdir -p bin/classes/com/
aapt p -f -v -M AndroidManifest.xml -F ./bin/resources.apk -I ~/system/classes/android.jar -S ./res -A ./assets/ -J gen/com/github/shnorbluk/telecharbanque
javac -sourcepath ./gen:src -d bin/classes src/com/github/shnorbluk/telecharbanque/MainActivity.java 2>&1 | tee errors.txt 
grep error errors.txt && exit 1
dx --dex --verbose --no-strict --output=bin/Telecharbanque.dex bin/classes/com 
apkbuilder ./bin/Telecharbanque.apk -v -u -z ./bin/resources.apk -f ./bin/Telecharbanque.dex 
signer  ./bin/Telecharbanque.apk bin/Telecharbanque_signed.apk
am start -a android.intent.action.VIEW -t application/vnd.android.package-archive -d file:///sdcard/AppProjects/Telecharbanque/bin/Telecharbanque_signed.apk
