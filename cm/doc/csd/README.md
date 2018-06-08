#### 1、校验sdl文件
![](/cm/doc/csd/1校验sdl文件.png) 
```bash
java -jar .\cm\doc\schema-validator-5.12.1.jar -s .\cm\csd\descriptor\service.sdl
```

#### 2、编译并打包CSD
![](/cm/doc/csd/2csd打成jar包.png) 
```bash
cd .\cm\csd
jar -cvf .\..\..\target\HBOARD-0.4.2.jar *
```

## 注意事项
#### 1、scripts下的脚本执行不能是后台运行
#### 2、cboard需要jdk1.8及以上
#### 3、所有shell文件必须转成unix格式
