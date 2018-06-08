
[csd](/cm/doc/csd/README.md)

[parcels](/cm/doc/parcels/README.md)

[https://github.com/cloudera/cm_ext/wiki](https://github.com/cloudera/cm_ext/wiki)

### JAVA升级至JDK8
可以[全局配置]：主机 > 配置 > 高级 > Java 主目录 > /usr/java/jdk1.8.0_161 
<br/>
也可以[单独配置]：HBoard > 配置 > 服务范围 > 高级 > HBoard 服务环境高级配置代码段（安全阀） > JAVA_HOME=/usr/java/jdk1.8.0_161

### 必须在Linux环境下才能生成parcel的manifest.json文件
mvn clean verify -Pcm
#### 注：在windows上检查parcel和生成manifest.json有问题

### 所有shell文件必须转成unix格式
dos2unix cm/parcels/bin/hboard
dos2unix cm/parcels/lib/hboard/bin/hboard.sh
dos2unix cm/parcels/meta/hboard_env.sh
dos2unix cm/csd/scripts/control.sh

### 使用新的parcel之前需要清空本地缓存
rm -rf /opt/cloudera/parcel-cache/HBOARD*
rm -rf /opt/cloudera/parcels/.flood/HBOARD*

### 使用新的CSD之前需要清空本地缓存
rm -rf /var/run/cloudera-scm-agent/process/*